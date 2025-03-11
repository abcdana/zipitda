package com.danahub.zipitda.community.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponseDto(
        Long id,
        Long userId,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int likeCount,
        int bookmarkCount,
        List<String> imageUrls
) {}