package com.danahub.zipitda.user.dto;

public record UserRegisterRequestDto (
        String email,       // 사용자 이메일
        String password,    // 패스워드
        String nickname     // 닉네임
) { }