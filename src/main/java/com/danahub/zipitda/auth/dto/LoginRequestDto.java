package com.danahub.zipitda.auth.dto;


public record LoginRequestDto (
        String email,
        String password
) {}