package com.danahub.zipitda.auth.service;

import com.danahub.zipitda.auth.dto.LoginRequestDto;
import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.dto.UserResponseDto;
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
    private final VerificationService verificationService;

    private static final String ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN:";
    private static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN:";

    public LoginResponseDto login(LoginRequestDto request) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ZipitdaException(ErrorType.INVALID_CREDENTIALS);
        }

        // 기존 토큰 무효화
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + user.getEmail());
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + user.getEmail());

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        // Redis에 Access Token과 Refresh Token 저장
        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + user.getEmail(), accessToken, 30, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + user.getEmail(), refreshToken, 7, TimeUnit.DAYS);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    public UserResponseDto getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getIsActive(),
                user.getProfileImage()
        );
    }

    public void logout(String email) {
        // Redis에서 Access Token과 Refresh Token 삭제
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + email);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
    }


    public boolean isTokenValid(String email, String token) {
        // Redis에 저장된 Access Token과 비교
        String storedAccessToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + email);
        return storedAccessToken != null && storedAccessToken.equals(token);
    }

    public void changePassword(String email, String newPassword) {

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        // 인증된 사용자 여부 확인 - 인증 코드가 검증되었는지 체크
        if (!verificationService.isVerified(email)) {
            throw new ZipitdaException(ErrorType.VERIFICATION_NOT_COMPLETED);
        }

        // 비밀번호 해싱 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new ZipitdaException(ErrorType.INVALID_TOKEN);
        }

        // Refresh Token에서 이메일 추출
        String email = jwtProvider.getEmailFromToken(refreshToken);

        // Redis에서 저장된 Refresh Token 가져오기
        String storedRefreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);

        // Redis에 저장된 Refresh Token과 일치하는지 확인
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new ZipitdaException(ErrorType.INVALID_TOKEN);
        }

        // 새로운 Access Token 생성
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole());

        // 새로운 Access Token을 Redis에 저장 (기존 Access Token 무효화)
        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + email, newAccessToken, 30, TimeUnit.MINUTES);

        return newAccessToken;
    }
}