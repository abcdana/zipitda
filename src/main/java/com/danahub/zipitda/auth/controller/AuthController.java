package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Void> checkNickname(@RequestParam String nickname) {
        if (userService.isNicknameAvailable(nickname)) {
            return ResponseEntity.ok().build(); // 닉네임 사용 가능 (200 OK)
        } else {
            return ResponseEntity.status(409).build(); // 닉네임 중복 (409 Conflict)
        }
    }
}