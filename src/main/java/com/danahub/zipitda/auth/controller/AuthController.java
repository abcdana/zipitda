package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.LoginRequestDto;
import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.auth.dto.LogoutRequestDto;
import com.danahub.zipitda.auth.dto.PasswordChangeRequestDto;
import com.danahub.zipitda.auth.service.AuthService;
import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.user.dto.UserResponseDto;
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

    @GetMapping("/user-info")
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.")
    public CommonResponse<UserResponseDto> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        return CommonResponse.success(authService.getUserInfo(jwtToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "사용자의 인증 세션을 종료하고, 서버에서 리프레시 토큰을 무효화합니다.")
    public CommonResponse<String> logout(@RequestBody LogoutRequestDto request) {
        authService.logout(request.email());
        return CommonResponse.success(request.email()+"님이 로그아웃 했습니다.");
    }

    @PutMapping("/changePassword")
    @Operation(summary = "비밀번호 변경 API", description = "기존 비밀번호를 삭제하고 새 비밀번호를 설정합니다.")
    public CommonResponse<String> changePassword(@RequestBody PasswordChangeRequestDto request) {
        authService.changePassword(request.email(), request.newPassword());
        return CommonResponse.success("비밀번호가 성공적으로 변경되었습니다.");
    }
}