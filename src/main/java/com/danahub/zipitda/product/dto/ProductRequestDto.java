package com.danahub.zipitda.product.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record ProductRequestDto(
        @NotBlank(message = "상품명을 입력하세요.")
        String name,
        @NotBlank(message = "상품 설명을 입력하세요.")
        String description,
        @NotBlank(message = "카테고리를 입력하세요.")
        String category,
        @NotNull(message = "가격을 입력하세요.")
        @Positive Long price,
        @NotNull(message = "재고 수량을 입력하세요.")
        @PositiveOrZero Long stockQuantity,
        List<String> imageUrls
) {}