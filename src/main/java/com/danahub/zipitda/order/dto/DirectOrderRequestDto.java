package com.danahub.zipitda.order.dto;

import jakarta.validation.constraints.NotNull;

public record DirectOrderRequestDto(
        @NotNull
        Long productId,
        @NotNull
        int quantity,
        @NotNull
        ShippingRequestDto shippingInfo,
        @NotNull
        PaymentRequestDto paymentInfo
) {}