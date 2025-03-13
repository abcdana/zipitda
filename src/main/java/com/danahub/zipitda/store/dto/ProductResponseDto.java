package com.danahub.zipitda.store.dto;

import java.time.LocalDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String categoryName,
        Long price,
        Long stockQuantity,
        LocalDateTime createdAt
) {}