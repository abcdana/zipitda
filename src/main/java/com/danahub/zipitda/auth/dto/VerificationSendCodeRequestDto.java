package com.danahub.zipitda.auth.dto;

import com.danahub.zipitda.auth.domain.VerificationType;

public record VerificationSendCodeRequestDto(
        VerificationType type,
        String recipient,
        String nickname
) {}