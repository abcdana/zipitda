package com.danahub.zipitda.auth.dto;

import lombok.Data;

@Data
public class VerificationVerifyCodeRequestDto {
    private String type;       // 인증 유형 (email/sms)
    private String recipient;  // 수신자 (이메일/전화번호)
    private String code;       // 인증 코드
}
