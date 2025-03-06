package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.VerificationResponseDto;
import com.danahub.zipitda.auth.dto.VerificationSendCodeRequestDto;
import com.danahub.zipitda.auth.dto.VerificationVerifyCodeRequestDto;
import com.danahub.zipitda.auth.service.VerificationService;
import com.danahub.zipitda.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@Tag(name = "Verification", description = "인증 API")
public class VerificationController {

    private final VerificationService verificationService;

    // 인증 코드 발송 API
    @PostMapping("/send-code")
    @Operation(summary = "인증 코드 발송 API", description = "이메일 또는 전화번호로 인증 코드를 발송합니다.")
    public CommonResponse<VerificationResponseDto> sendVerificationCode(
            @RequestBody VerificationSendCodeRequestDto request) {

        VerificationResponseDto response = verificationService.sendCode(request);
        return CommonResponse.success(response);
    }

    // 인증 코드 검증 API
    @PostMapping("/verify-code")
    @Operation(summary = "인증 코드 검증 API", description = "사용자가 입력한 인증 코드가 유효한지 검증합니다.")
    public CommonResponse<VerificationResponseDto> verifyCode(
            @RequestBody VerificationVerifyCodeRequestDto request) {

        VerificationResponseDto response = verificationService.verifyCode(request);
        return CommonResponse.success(response);
    }
}