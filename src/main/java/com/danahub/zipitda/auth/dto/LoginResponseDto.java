package com.danahub.zipitda.auth.dto;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {}