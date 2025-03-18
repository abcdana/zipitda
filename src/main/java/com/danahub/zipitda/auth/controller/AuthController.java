package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.LoginRequestDto;
import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.auth.service.AuthService;
import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.user.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증/허가 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "사용자가 이메일, 비밀번호를 입력하면 JWT 액세스 토큰/리프레시 토큰을 발급하여 로그인합니다.")
    public CommonResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return CommonResponse.success(authService.login(request));
    }
    @GetMapping("/user-info")
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.")
    public CommonResponse<UserResponseDto> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        return CommonResponse.success(authService.getUserInfo(email));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "사용자의 인증 세션을 종료하고, 서버에서 리프레시 토큰을 무효화합니다.")
    public void logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken); // 이제 email 대신 JWT Token을 이용해서 로그아웃 처리
        CommonResponse.success("로그아웃 되었습니다.");
    }

    @PutMapping("/changePassword")
    @Operation(summary = "비밀번호 변경 API", description = "기존 비밀번호를 삭제하고 새 비밀번호를 설정합니다.")
    public void changePassword(@RequestBody String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자 이메일 가져오기
        authService.changePassword(email, newPassword);
        CommonResponse.success("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신 API", description = "Refresh Token을 이용하여 새로운 Access Token을 발급합니다.")
    public CommonResponse<String> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken.replace("Bearer ", ""));
        return CommonResponse.success(newAccessToken);
    }
}