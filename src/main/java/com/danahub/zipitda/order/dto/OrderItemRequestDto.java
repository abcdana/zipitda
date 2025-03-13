package com.danahub.zipitda.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public record OrderItemRequestDto (
    @NotNull
    Long productId,
    @Min(1)
    int quantity,
    @NotNull
    BigDecimal price
){ }