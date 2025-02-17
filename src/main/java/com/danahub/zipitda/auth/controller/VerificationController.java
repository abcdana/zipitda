package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.VerificationResponseDto;
import com.danahub.zipitda.auth.dto.VerificationSendCodeRequestDto;
import com.danahub.zipitda.auth.dto.VerificationVerifyCodeRequestDto;
import com.danahub.zipitda.auth.service.VerificationService;
import com.danahub.zipitda.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    // 인증 코드 발송 API
    @PostMapping("/send-code")
    public CommonResponse<VerificationResponseDto> sendVerificationCode(
            @RequestBody VerificationSendCodeRequestDto request) {

        VerificationResponseDto response = verificationService.sendCode(request);
        return CommonResponse.success(response);
    }

    // 인증 코드 검증 API
    @PostMapping("/verify-code")
    public CommonResponse<VerificationResponseDto> verifyCode(
            @RequestBody VerificationVerifyCodeRequestDto request) {

        VerificationResponseDto response = verificationService.verifyCode(request);
        return CommonResponse.success(response);
    }
}