package com.danahub.zipitda.auth.dto;

import com.danahub.zipitda.auth.domain.VerificationType;

public record VerificationVerifyCodeRequestDto(
        VerificationType type,
        String recipient,
        String code
) { }