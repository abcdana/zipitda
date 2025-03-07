package com.danahub.zipitda.community.dto;

public record PostRequestDto (
        Long postId,
        Long userId,
        String title,
        String content
){ }
