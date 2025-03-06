package com.danahub.zipitda.community.dto;

public record PostRequestDto (
        Long userId,
        String title,
        String content
){ }
