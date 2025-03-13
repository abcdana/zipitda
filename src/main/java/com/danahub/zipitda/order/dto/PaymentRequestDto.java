package com.danahub.zipitda.order.dto;

import com.danahub.zipitda.order.domain.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;

public record PaymentRequestDto(
    @NotNull
    PaymentMethod paymentMethod,
    @Min(1)
    BigDecimal amount
) { }
