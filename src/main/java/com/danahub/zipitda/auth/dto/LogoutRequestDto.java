package com.danahub.zipitda.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public record LogoutRequestDto (
    @NotBlank
    @Email
    String email
) { }