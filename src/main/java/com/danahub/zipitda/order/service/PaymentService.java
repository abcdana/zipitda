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
    @Transactional
    public void processPaymentCallback(PaymentCallbackRequestDto dto) {
        Order order = orderRepository.findByOrderNumber(dto.orderNumber())
                .orElseThrow(() -> new ZipitdaException(ErrorType.ORDER_NOT_FOUND));

        Payment payment = order.getPayment();

        for (OrderItem item : order.getOrderItems()) {
            Long productId = item.getProduct().getId();
            int quantity = item.getQuantity();

            RLock lock = redissonClient.getLock("lock:product:" + productId);
            try {
                if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> {
                                failPayment(payment, dto.transactionId(), "상품 정보 없음");
                                return new ZipitdaException(ErrorType.PRODUCT_NOT_FOUND);
                            });

                    if (product.getStockQuantity() < quantity) {
                        failPayment(payment, dto.transactionId(), "재고 부족");
                        throw new ZipitdaException(ErrorType.OUT_OF_STOCK);
                    }

                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    productRepository.save(product);
                } else {
                    failPayment(payment, dto.transactionId(), "락 획득 실패");
                    throw new ZipitdaException(ErrorType.LOCK_FAILED);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failPayment(payment, dto.transactionId(), "락 인터럽트 발생");
                throw new ZipitdaException(ErrorType.LOCK_FAILED);
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

    // ️결제 실패 처리 메서드
    private void failPayment(Payment payment, String transactionId, String reason) {
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailedReason(reason);
        payment.setPaidAt(LocalDateTime.now());
    }
}
