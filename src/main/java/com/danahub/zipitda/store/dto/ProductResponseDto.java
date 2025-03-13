package com.danahub.zipitda.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String categoryName,
        BigDecimal price,
        int stockQuantity,
        LocalDateTime createdAt
) {}