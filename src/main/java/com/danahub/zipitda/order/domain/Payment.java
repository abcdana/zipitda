package com.danahub.zipitda.order.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 결제 PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;  // 주문 ID (FK)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway", nullable = false, length = 50)
    private PaymentGateway paymentGateway;  // PG사 정보 (KAKAO, TOSS)

    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;  // 결제 거래 고유 ID (PG사 제공)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;  // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;  // 결제 완료 시각

    @Column(name = "failed_reason", length = 255)
    private String failedReason;  // 결제 실패 사유
}