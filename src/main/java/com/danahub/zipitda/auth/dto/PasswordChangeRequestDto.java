package com.danahub.zipitda.auth.dto;

public record PasswordChangeRequestDto(
        String email,
        String newPassword
) { }