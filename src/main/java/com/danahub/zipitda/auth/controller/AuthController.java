package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.LoginRequestDto;
import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.auth.service.AuthService;
import com.danahub.zipitda.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증/허가 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "사용자가 이메일, 비밀번호를 입력하면\n" +
            "JWT 액세스 토큰/리프레시 토큰을 발급하여 로그인합니다.")
    public CommonResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return CommonResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "사용자의 인증 세션을 종료하고, 서버에서 리프레시 토큰을 무효화합니다.")
    public void logout(@RequestParam String email) {
        authService.logout(email);
    }
}