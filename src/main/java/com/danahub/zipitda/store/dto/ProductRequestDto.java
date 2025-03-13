package com.danahub.zipitda.store.dto;

import com.danahub.zipitda.store.domain.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDto(
        @NotBlank(message = "상품명을 입력하세요.")
        String name,
        @NotBlank(message = "상품 설명을 입력하세요.")
        String description,
        @NotBlank(message = "카테고리를 입력하세요.")
        Long categoryId,
        @NotNull(message = "가격을 입력하세요.")
        @Positive BigDecimal price,
        @NotNull(message = "재고 수량을 입력하세요.")
        @PositiveOrZero int stockQuantity,
        List<String> imageUrls
) { }