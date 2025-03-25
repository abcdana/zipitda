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
    private final ShippingRepository shippingRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ImageRepository imageRepository;
    private final OrderNumberGenerator orderNumberGenerator;

    // 장바구니 주문 생성
    @Transactional
    public PaymentRequestDtoForPG createOrderFromCart(
            CustomUserDetails userDetails,
            ShippingRequestDto shippingDto,
            PaymentRequestDto paymentDto
    ) {
        // 1) 사용자, 장바구니 조회
        User user = findUserOrThrow(userDetails.getUserId());
        List<Cart> selectedCarts = getSelectedCartsOrThrow(user.getId());

        // 2) Order 엔티티 생성 후 저장
        String orderNumber = orderNumberGenerator.generate("ORD");
        Order order = createEmptyOrder(user, orderNumber);

        // 3) 장바구니 → OrderItem 변환, 재고 확인, 금액 계산
        BigDecimal totalPrice = addOrderItemsFromCart(order, selectedCarts);

        // 4) 주문 금액 업데이트
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // 5) 배송 정보 생성
        createShipping(order, shippingDto);

        // 6) 결제 정보 생성
        Payment payment = createPayment(order, paymentDto, totalPrice);

        // 7) 장바구니 비우기
        cartRepository.deleteAll(selectedCarts);

        log.info("장바구니 주문 완료 - OrderNumber: {}, UserId: {}", orderNumber, user.getId());

        // PG 요청 정보 리턴
        return new PaymentRequestDtoForPG(
                order.getOrderNumber(),
                totalPrice,
                user.getEmail(),
                "총 " + selectedCarts.size() + "건",
                payment.getPaymentGateway().name()
        );
    }

    // 단일 상품 주문 (직접 구매)
    @Transactional
    public PaymentRequestDtoForPG createDirectOrder(
            CustomUserDetails userDetails,
            DirectOrderRequestDto requestDto
    ) {
        // 1) 사용자, 상품 조회
        User user = findUserOrThrow(userDetails.getUserId());
        Product product = findProductOrThrow(requestDto.productId());

        // 2) 재고 확인
        int quantity = requestDto.quantity();
        checkStockOrThrow(product, quantity);

        // 3) 주문 생성
        String orderNumber = orderNumberGenerator.generate("ORD");
        Order order = createEmptyOrder(user, orderNumber);

        // 4) OrderItem 생성
        BigDecimal totalPrice = addSingleOrderItem(order, product, quantity);

        // 5) 배송 정보 생성
        createShipping(order, requestDto.shippingInfo());

        // 6) 결제 정보 생성
        Payment payment = createPayment(order, requestDto.paymentInfo(), totalPrice);

        log.info("단일 상품 주문 완료 - OrderNumber: {}, UserId: {}, ProductId: {}",
                orderNumber, user.getId(), product.getId());

        return new PaymentRequestDtoForPG(
                order.getOrderNumber(),
                totalPrice,
                user.getEmail(),
                "총 1 건",
                payment.getPaymentGateway().name()
        );
    }

    //------------------------------------------------------------------------------
    // 세부 로직 메서드
    //------------------------------------------------------------------------------

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));
    }

    private List<Cart> getSelectedCartsOrThrow(Long userId) {
        List<Cart> carts = cartRepository.findByUserIdAndSelectedTrue(userId);
        if (carts.isEmpty()) {
            throw new ZipitdaException(ErrorType.CART_EMPTY);
        }
        return carts;
    }

    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.PRODUCT_NOT_FOUND));
    }

    private void checkStockOrThrow(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new ZipitdaException(ErrorType.OUT_OF_STOCK, Map.of("productId", product.getId()));
        }
    }

    // 주문 엔티티 생성 (status=CREATED, 임시 totalPrice=0)
    private Order createEmptyOrder(User user, String orderNumber) {
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO) // 임시
                .build();
        orderRepository.save(order);
        return order;
    }

    /**
     * 장바구니 -> OrderItem
     * - 재고 확인
     * - 썸네일 조회
     * - OrderItem 생성, order에 add
     * - 총 금액 계산
     */
    private BigDecimal addOrderItemsFromCart(Order order, List<Cart> selectedCarts) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : selectedCarts) {
            Product product = cart.getProduct();
            int quantity = cart.getQuantity();

            checkStockOrThrow(product, quantity);

            Image thumbnail = findThumbnailOrThrow(product.getId());

            // OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .productNameSnapshot(product.getName())
                    .thumbnailUrl(thumbnail.getImageUrl())
                    .build();

            order.addOrderItem(orderItem);

            // 금액 합산
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        return totalPrice;
    }

    // 단일 상품 주문 - OrderItem 생성
    private BigDecimal addSingleOrderItem(Order order, Product product, int quantity) {
        Image thumbnail = findThumbnailOrThrow(product.getId());

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .productNameSnapshot(product.getName())
                .thumbnailUrl(thumbnail.getImageUrl())
                .build();

        order.addOrderItem(orderItem);

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        return totalPrice;
    }

    // 이미지 조회
    private Image findThumbnailOrThrow(Long productId) {
        return imageRepository
                .findFirstByTargetTypeAndTargetIdAndThumbnailYnTrue(TargetType.PRODUCT, productId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.IMAGE_NOT_FOUND));
    }

    // 배송 정보 생성
    private void createShipping(Order order, ShippingRequestDto shippingDto) {
        Shipping shipping = Shipping.builder()
                .order(order)
                .recipientName(shippingDto.recipientName())
                .address(shippingDto.address())
                .phone(shippingDto.phone())
                .status(ShippingStatus.PENDING)
                .build();
        shippingRepository.save(shipping);
    }

    // 결제 정보 생성
    private Payment createPayment(Order order, PaymentRequestDto paymentDto, BigDecimal totalPrice) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentDto.paymentMethod())
                .paymentGateway(paymentDto.paymentGateway())
                .transactionId("TEMP-" + UUID.randomUUID())  // 임시 ID
                .status(PaymentStatus.PENDING)
                .amount(totalPrice)
                .build();
        paymentRepository.save(payment);
        return payment;
    }

}