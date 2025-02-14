package com.danahub.zipitda.auth.dto;

public record VerificationSendCodeRequestDto(
        String type,
        String recipient
) { }