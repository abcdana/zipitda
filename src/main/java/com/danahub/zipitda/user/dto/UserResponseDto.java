package com.danahub.zipitda.user.dto;

public record UserResponseDto (
        String email,       // 이메일 (로그인시 사용)
        String nickname,    // 닉네임
        String isActive     // 활성상태인지 확인용
) { }
