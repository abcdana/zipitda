package com.danahub.zipitda.order.dto;

import com.danahub.zipitda.order.domain.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ShippingRequestDto(
        @NotBlank
        String recipientName,
        @NotBlank
        String address,
        @NotBlank
        String phone
) { }
