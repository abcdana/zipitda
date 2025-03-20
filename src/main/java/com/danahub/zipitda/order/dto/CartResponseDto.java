package com.danahub.zipitda.order.dto;

import java.math.BigDecimal;

public record CartResponseDto(
        Long cartId,
        Long productId,
        String productName,
        BigDecimal price,
        int quantity,
        boolean selected
) {}