package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.LoginRequestDto;
import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.auth.service.AuthService;
import com.danahub.zipitda.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return CommonResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public CommonResponse<String> logout(@RequestParam String email) {
        authService.logout(email);
        return CommonResponse.success("로그아웃 성공");
    }
}