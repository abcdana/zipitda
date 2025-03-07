package com.danahub.zipitda.community.dto;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        Long userId,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}