package com.danahub.zipitda.auth.dto;

public record VerificationResponseDto(
        boolean success,     // 요청 성공 여부
        String message,      // 응답 메시지
        int retryCount,      // 남은 재시도 횟수
        String expiredAt     // 인증코드 만료시간
) { }