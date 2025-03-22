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

        // 분산락 획득 및 재고 차감
        for (OrderItem item : order.getOrderItems()) {
            Long productId = item.getProduct().getId();
            int quantity = item.getQuantity();

            RLock lock = redissonClient.getLock("lock:product:" + productId);
            try {
                if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ZipitdaException(ErrorType.PRODUCT_NOT_FOUND));

                    if (product.getStockQuantity() < quantity) {
                        throw new ZipitdaException(ErrorType.OUT_OF_STOCK);
                    }

                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    productRepository.save(product);
                } else {
                    throw new ZipitdaException(ErrorType.LOCK_FAILED);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ZipitdaException(ErrorType.LOCK_FAILED);
            } finally {
                lock.unlock();
            }
        }

        // 결제 성공 상태 업데이트
        Payment payment = order.getPayment();
        payment.setTransactionId(dto.transactionId());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        order.setStatus(OrderStatus.CONFIRMED);
    }
}
