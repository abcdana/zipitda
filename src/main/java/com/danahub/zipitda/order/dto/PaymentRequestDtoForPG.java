package com.danahub.zipitda.order.dto;

import java.math.BigDecimal;

public record PaymentRequestDtoForPG(
        String orderNumber,
        BigDecimal amount,
        String userEmail,
        String productSummary,
        String pgProvider
) {}