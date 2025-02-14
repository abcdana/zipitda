package com.danahub.zipitda.auth.dto;

public record VerificationVerifyCodeRequestDto(
        String type,
        String recipient,
        String code
) { }