package com.danahub.zipitda.product.dto;

import java.time.LocalDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String category,
        Long price,
        Long stockQuantity,
        LocalDateTime createdAt
) {}