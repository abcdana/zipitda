package com.danahub.zipitda.order.domain;

import com.danahub.zipitda.common.domain.BaseEntity;
import com.danahub.zipitda.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 주문한 사용자

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber; // 주문 고유 번호 (노출용)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice; // 총 주문 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.CREATED; // 주문 생성 시 기본 상태

    @Column(name = "canceled_at")
    private java.time.LocalDateTime canceledAt; // 주문 취소 시각

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason; // 주문 취소 사유

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipping shipping;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}