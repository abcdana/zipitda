package com.danahub.zipitda.user.dto;

import com.danahub.zipitda.user.domain.User;

public record UserResponseDto (
        String email,       // 이메일 (로그인시 사용)
        String nickname,    // 닉네임
        String isActive,    // 활성상태인지 확인용
        String profileImage // 프로필 이미지
) {
    public UserResponseDto(User user) {
        this(user.getEmail(), user.getNickname(), user.getIsActive(), user.getProfileImage());
    }
}
