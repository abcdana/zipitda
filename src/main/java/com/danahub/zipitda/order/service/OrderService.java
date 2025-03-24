package com.danahub.zipitda.order.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.common.security.CustomUserDetails;
import com.danahub.zipitda.common.util.OrderNumberGenerator;
import com.danahub.zipitda.community.domain.Image;
import com.danahub.zipitda.community.domain.TargetType;
import com.danahub.zipitda.community.repository.ImageRepository;
import com.danahub.zipitda.order.domain.*;
import com.danahub.zipitda.order.dto.*;
import com.danahub.zipitda.order.repository.*;
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
import java.util.UUID;

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
    private final CartRepository cartRepository;
    private final ImageRepository imageRepository;
    private final OrderNumberGenerator orderNumberGenerator;
    @Transactional
    public PaymentRequestDtoForPG createOrderFromCart(CustomUserDetails userDetails, ShippingRequestDto shippingDto, PaymentRequestDto paymentDto) {
        Long userId = userDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        // 선택된 장바구니 조회
        List<Cart> selectedCarts = cartRepository.findByUserIdAndSelectedTrue(userId);
        if (selectedCarts.isEmpty()) {
            throw new ZipitdaException(ErrorType.CART_EMPTY);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        // 주문번호 생성
        String orderNumber = orderNumberGenerator.generate("ORD");

        // 주문 먼저 생성 (OrderItem 나중에 연결)
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO) // 임시값
                .build();
        orderRepository.save(order);

        // 장바구니 -> OrderItem으로 변환 후 편의 메서드로 연결
        for (Cart cart : selectedCarts) {
            Product product = cart.getProduct();
            int quantity = cart.getQuantity();

            // 재고 확인
            if (product.getStockQuantity() < quantity) {
                throw new ZipitdaException(ErrorType.OUT_OF_STOCK, Map.of("productId", product.getId()));
            }

            // 썸네일 이미지 조회
            Image thumbnailImage = imageRepository
                    .findFirstByTargetTypeAndTargetIdAndThumbnailYnTrue(TargetType.PRODUCT, product.getId())
                    .orElseThrow(() -> new ZipitdaException(ErrorType.IMAGE_NOT_FOUND));

            // OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .productNameSnapshot(product.getName())
                    .thumbnailUrl(thumbnailImage.getImageUrl())
                    .build();

            order.addOrderItem(orderItem);  // ✅ 편의 메서드로 양방향 세팅

            // 금액 누적
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        // 총 금액 업데이트
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // 배송 정보 저장
        Shipping shipping = Shipping.builder()
                .order(order)
                .recipientName(shippingDto.recipientName())
                .address(shippingDto.address())
                .phone(shippingDto.phone())
                .status(ShippingStatus.PENDING)
                .build();
        shippingRepository.save(shipping);

        // 결제 정보 저장 (PG 결제 호출 전)
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentDto.paymentMethod())
                .paymentGateway(paymentDto.paymentGateway())
                .transactionId("TEMP-" + UUID.randomUUID())  // TODO. 임시 트랜잭션 ID 고칠 것
                .status(PaymentStatus.PENDING)
                .amount(totalPrice)
                .build();
        paymentRepository.save(payment);

        // 장바구니 비우기
        cartRepository.deleteAll(selectedCarts);

        log.info("장바구니 주문 완료 - OrderNumber: {}, UserId: {}", orderNumber, userId);

        // PG 요청 정보 구성
        return new PaymentRequestDtoForPG(
                order.getOrderNumber(),
                totalPrice,
                user.getEmail(),
                "총 " + selectedCarts.size() + "건",
                paymentDto.paymentGateway().name()
        );
    }

    @Transactional
    public PaymentRequestDtoForPG createDirectOrder(CustomUserDetails userDetails, DirectOrderRequestDto requestDto) {
        Long userId = userDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        Product product = productRepository.findById(requestDto.productId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.PRODUCT_NOT_FOUND));

        int quantity = requestDto.quantity();

        // 재고 확인
        if (product.getStockQuantity() < quantity) {
            throw new ZipitdaException(ErrorType.OUT_OF_STOCK, Map.of("productId", product.getId()));
        }

        // 썸네일 이미지 조회
        Image thumbnailImage = imageRepository
                .findFirstByTargetTypeAndTargetIdAndThumbnailYnTrue(TargetType.PRODUCT, product.getId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.IMAGE_NOT_FOUND));

        // 주문번호 생성
        String orderNumber = orderNumberGenerator.generate("ORD");

        // 주문 생성 (먼저 생성해서 OrderItem과 연결해야 함)
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO) // 임시 설정, 아래에서 업데이트
                .build();

        // OrderItem 생성 및 연관관계 세팅
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .productNameSnapshot(product.getName())
                .thumbnailUrl(thumbnailImage.getImageUrl())
                .build();

        // 양방향 연관관계 세팅
        order.addOrderItem(orderItem);

        // 총 금액 계산
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(totalPrice);

        // 주문 저장
        orderRepository.save(order);

        // 배송 정보 저장
        Shipping shipping = Shipping.builder()
                .order(order)
                .recipientName(requestDto.shippingInfo().recipientName())
                .address(requestDto.shippingInfo().address())
                .phone(requestDto.shippingInfo().phone())
                .status(ShippingStatus.PENDING)
                .build();
        shippingRepository.save(shipping);

        // 결제 정보 저장 (임시 트랜잭션 ID 사용)
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(requestDto.paymentInfo().paymentMethod())
                .paymentGateway(requestDto.paymentInfo().paymentGateway())
                .transactionId("TEMP-" + UUID.randomUUID()) // 🔥 임시 트랜잭션 ID
                .status(PaymentStatus.PENDING)
                .amount(totalPrice)
                .build();
        paymentRepository.save(payment);

        log.info("단일 상품 주문 생성 완료 - OrderNumber: {}, UserId: {}, ProductId: {}", order.getOrderNumber(), userId, product.getId());

        return new PaymentRequestDtoForPG(
                order.getOrderNumber(),
                totalPrice,
                user.getEmail(),
                "총 1 건",
                payment.getPaymentGateway().name()
        );
    }
}