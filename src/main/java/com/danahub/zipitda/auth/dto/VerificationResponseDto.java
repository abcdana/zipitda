package com.danahub.zipitda.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResponseDto {
    private boolean success;     // 요청 성공 여부
    private String message;      // 응답 메시지
    private int retryCount;      // 남은 재시도 횟수
    private String expiredAt;    // 인증코드 만료시간
}