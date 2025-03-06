package com.danahub.zipitda.auth.dto;

import com.danahub.zipitda.user.dto.UserResponseDto;

public record SocialLoginResponseDto(
        String accessToken,
        String refreshToken,
        UserResponseDto user
) {}