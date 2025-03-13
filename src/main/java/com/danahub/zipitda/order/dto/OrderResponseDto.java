package com.danahub.zipitda.order.dto;

import com.danahub.zipitda.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        Long orderId,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) { }
