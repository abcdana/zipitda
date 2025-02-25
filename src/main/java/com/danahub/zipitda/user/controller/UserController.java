package com.danahub.zipitda.user.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.dto.UserRegisterRequestDto;
import com.danahub.zipitda.user.dto.UserResponseDto;
import com.danahub.zipitda.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() { //TODO. CommonResponse로 고치기
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Void> checkNickname(@RequestParam String nickname) {
        if (userService.isNicknameAvailable(nickname)) {
            return ResponseEntity.ok().build(); // 닉네임 사용 가능 (200 OK)
        } else {
            return ResponseEntity.status(409).build(); // 닉네임 중복 (409 Conflict)
        }
    }

    // 회원가입 API
    @PostMapping("/register")
    public CommonResponse<UserResponseDto> registerUser(
            @RequestBody UserRegisterRequestDto request) {

        UserResponseDto response = userService.registerUser(request);
        return CommonResponse.success(response);
    }
}