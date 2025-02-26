package com.danahub.zipitda.auth.service;

import com.danahub.zipitda.auth.dto.LoginRequestDto;
import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.repository.UserRepository;
import com.danahub.zipitda.common.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "TOKEN:";

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ZipitdaException(ErrorType.INVALID_CREDENTIALS);
        }

        // 기존 토큰 무효화 (새로 로그인 시 이전 AccessToken 삭제)
        String existingToken = redisTemplate.opsForValue().get(TOKEN_PREFIX + user.getEmail());
        if (existingToken != null) {
            redisTemplate.delete(TOKEN_PREFIX + user.getEmail());
        }

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        // Redis에 Access Token 저장 (만료 시간: 30분)
        redisTemplate.opsForValue().set(TOKEN_PREFIX + user.getEmail(), accessToken, 30, TimeUnit.MINUTES);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String email) {
        // Redis에서 토큰 무효화
        String tokenKey = TOKEN_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey))) {
            redisTemplate.delete(tokenKey);
        } else {
            throw new ZipitdaException(ErrorType.INVALID_CREDENTIALS);
        }
    }

    public boolean isTokenValid(String email, String token) {
        // Redis에 저장된 토큰과 비교
        String storedToken = redisTemplate.opsForValue().get(TOKEN_PREFIX + email);
        return storedToken != null && storedToken.equals(token);
    }
}