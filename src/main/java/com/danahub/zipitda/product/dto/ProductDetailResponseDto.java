package com.danahub.zipitda.product.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResponseDto(
        Long id,
        Long userId,
        String name,
        String description,
        String category,
        Long price,
        Long stockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<String> imageUrls
) {}