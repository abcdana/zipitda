package com.danahub.zipitda.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record OrderRequestDto (

    @NotNull
    List<OrderItemRequestDto> orderItems,
    @NotNull
    ShippingRequestDto shippingInfo,
    @NotNull
    PaymentRequestDto paymentInfo
){ }