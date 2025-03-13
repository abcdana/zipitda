package com.danahub.zipitda.store.dto;

public record CartResponseDto(
        Long cartId,
        Long productId,
        String productName,
        Long price,
        int quantity
) {}