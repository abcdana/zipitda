package com.danahub.zipitda.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartRequestDto(
        @NotNull
        Long productId,
        @Min(1)
        int quantity
) {}