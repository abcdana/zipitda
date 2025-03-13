package com.danahub.zipitda.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResponseDto(
        Long id,
        Long userId,
        String name,
        String description,
        String categoryName,
        BigDecimal price,
        int stockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<String> imageUrls
) {}