package com.danahub.zipitda.order.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.order.domain.*;
import com.danahub.zipitda.order.dto.PaymentCallbackRequestDto;
import com.danahub.zipitda.order.repository.OrderRepository;
import com.danahub.zipitda.store.domain.Product;
import com.danahub.zipitda.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;
    private final PaymentRecordService paymentRecordService;

    @Transactional
    public void processPaymentCallback(PaymentCallbackRequestDto dto) {
        Order order = orderRepository.findByOrderNumber(dto.orderNumber())
                .orElseThrow(() -> new ZipitdaException(ErrorType.ORDER_NOT_FOUND));

        Payment payment = order.getPayment();
        if (payment == null) {
            throw new ZipitdaException(ErrorType.PAYMENT_NOT_FOUND);
        }

        // 재고 차감 시도
        for (OrderItem item : order.getOrderItems()) {
            Long productId = item.getProduct().getId();
            int quantity = item.getQuantity();

            RLock lock = redissonClient.getLock("lock:product:" + productId);
            try {
                if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> {
                                // 재고 로드 실패
                                recordFailureAndThrow(payment.getId(), dto.transactionId(), "상품 정보 없음", ErrorType.PRODUCT_NOT_FOUND);
                                return null;
                            });

                    if (product.getStockQuantity() < quantity) {
                        // 재고 부족
                        recordFailureAndThrow(payment.getId(), dto.transactionId(), "재고 부족", ErrorType.OUT_OF_STOCK);
                    }

                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    productRepository.save(product);

                } else {
                    // 락 획득 실패
                    recordFailureAndThrow(payment.getId(), dto.transactionId(), "락 획득 실패", ErrorType.LOCK_FAILED);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 인터럽트 발생
                recordFailureAndThrow(payment.getId(), dto.transactionId(), "락 인터럽트 발생", ErrorType.LOCK_FAILED);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        // 재고 차감 성공 → 결제 성공 처리
        payment.setTransactionId(dto.transactionId());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        order.setStatus(OrderStatus.CONFIRMED);
    }


    private void recordFailureAndThrow(Long paymentId, String txId, String reason, ErrorType errorType) {
        // 결제 실패를 별도 트랜잭션에 기록
        paymentRecordService.failPaymentInNewTx(paymentId, txId, reason);

        throw new ZipitdaException(errorType);
    }
}
