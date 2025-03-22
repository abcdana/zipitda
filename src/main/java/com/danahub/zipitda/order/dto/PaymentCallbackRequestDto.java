package com.danahub.zipitda.order.dto;

import com.danahub.zipitda.order.domain.PaymentStatus;

import java.math.BigDecimal;

public record PaymentCallbackRequestDto(
        String orderNumber,         // 집잇다 주문 번호 (ORD20250322-xxxxxx)
        String transactionId,       // PG 거래 고유 ID
        BigDecimal amount,          // 결제된 금액
        PaymentStatus status,       // COMPLETED, FAILED
        String failReason           // 실패 사유 (null 가능)
) {}