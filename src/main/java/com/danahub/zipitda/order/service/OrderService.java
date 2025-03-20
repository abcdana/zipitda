package com.danahub.zipitda.order.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.order.domain.*;
import com.danahub.zipitda.order.dto.OrderItemRequestDto;
import com.danahub.zipitda.order.dto.PaymentRequestDto;
import com.danahub.zipitda.order.dto.ShippingRequestDto;
import com.danahub.zipitda.order.repository.OrderItemRepository;
import com.danahub.zipitda.order.repository.OrderRepository;
import com.danahub.zipitda.order.repository.PaymentRepository;
import com.danahub.zipitda.order.repository.ShippingRepository;
import com.danahub.zipitda.store.domain.Product;
import com.danahub.zipitda.store.repository.ProductRepository;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.repository.UserRepository;
import com.danahub.zipitda.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingRepository shippingRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public Long createOrder(Authentication authentication, List<OrderItemRequestDto> orderItems, ShippingRequestDto shippingInfo, PaymentRequestDto paymentInfo) {

        // 고객 정보 추출
        Long userId = userService.findUserIdByEmail(authentication.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        // 주문 생성 (Order 먼저 저장)
        Order order = Order.builder()
                .user(user)
                .totalPrice(BigDecimal.ZERO)  // 총 금액은 후에 업데이트
                .status(OrderStatus.CREATED)
                .build();
        orderRepository.save(order);

        // 장바구니 아이템 조회
        List<OrderItem> orderItemList = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : orderItems) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ZipitdaException(ErrorType.PRODUCT_NOT_FOUND));

            if (product.getStockQuantity() < itemDto.quantity()) {
                throw new ZipitdaException(ErrorType.OUT_OF_STOCK, Map.of("productId", product.getId()));
            }

            updateStock(product, itemDto.quantity());  // 재고 차감

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.quantity())
                    .price(product.getPrice())
                    .build();

            orderItemList.add(orderItem);
            totalPrice = totalPrice.add(orderItem.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));

        }

        orderItemRepository.saveAll(orderItemList);  // 주문 아이템 저장 최적화

        // 주문 가격 업데이트
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // 배송 정보 저장
        Shipping shipping = Shipping.builder()
                .order(order)
                .recipientName(shippingInfo.recipientName())
                .address(shippingInfo.address())
                .phone(shippingInfo.phone())
                .status(ShippingStatus.PENDING)
                .build();
        shippingRepository.save(shipping);

        // 결제 정보 저장
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentInfo.paymentMethod())
                .amount(totalPrice)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.info("주문 생성 완료  // OrderId: {}, UserId: {}, TotalPrice: {}", order.getId(), userId, totalPrice);
        return order.getId();
    }

    // 재고 차감
    private void updateStock(Product product, int quantity) {
        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);
        productRepository.save(product);
        log.info("재고 업데이트: ProductId: {}, 남은 재고: {}", product.getId(), newStock);
    }
}