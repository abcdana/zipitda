package com.danahub.zipitda.auth.service;

import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.auth.dto.SocialLoginResponseDto;
import com.danahub.zipitda.common.util.JwtProvider;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.dto.UserResponseDto;
import com.danahub.zipitda.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${kakao.client-id}") private String kakaoClientId;
    @Value("${kakao.client-secret}") private String kakaoClientSecret;
    @Value("${kakao.redirect-uri}") private String kakaoRedirectUri;
    @Value("${kakao.token-uri}") private String kakaoTokenUri;
    @Value("${kakao.user-info-uri}") private String kakaoUserInfoUri;

    @Value("${naver.client-id}") private String naverClientId;
    @Value("${naver.client-secret}") private String naverClientSecret;
    @Value("${naver.redirect-uri}") private String naverRedirectUri;
    @Value("${naver.token-uri}") private String naverTokenUri;
    @Value("${naver.user-info-uri}") private String naverUserInfoUri;

    // 카카오 로그인 처리
    @Transactional
    public SocialLoginResponseDto processKakaoLogin(String code) {
        String accessToken = getAccessToken(kakaoTokenUri, kakaoClientId, kakaoClientSecret, kakaoRedirectUri, code);
        Map<String, Object> userInfo = getUserInfo(kakaoUserInfoUri, accessToken);
        log.info("userInfo : " + userInfo);
        return handleUserLogin(userInfo, "KAKAO");
    }

    // 네이버 로그인 처리
    @Transactional
    public SocialLoginResponseDto processNaverLogin(String code) {
        String accessToken = getAccessToken(naverTokenUri, naverClientId, naverClientSecret, naverRedirectUri, code);
        Map<String, Object> userInfo = getUserInfo(naverUserInfoUri, accessToken);
        return handleUserLogin(userInfo, "NAVER");
    }

    // 공통 액세스 토큰 요청 메서드
    private String getAccessToken(String tokenUri, String clientId, String clientSecret, String redirectUri, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    // 공통 사용자 정보 조회 메서드
    private Map<String, Object> getUserInfo(String userInfoUri, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, Map.class);

        log.info("사용자 정보 응답: " + response.getBody());

        return response.getBody();
    }

    // 유저 정보 저장 및 로그인 처리
    private SocialLoginResponseDto handleUserLogin(Map<String, Object> userInfo, String provider) {
        String email = (String) ((Map<String, Object>) userInfo.get("kakao_account")).get("email");
        String nickname = (String) ((Map<String, Object>) userInfo.get("properties")).get("nickname");
        String profileImage = (String) ((Map<String, Object>) userInfo.get("properties")).get("profile_image");

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .isActive("ACTIVE") // 기본 활성화 상태로 설정
                    .role("USER") // 기본 역할 설정
                    .build();

            userRepository.save(user);
        }

        // JWT 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        return new SocialLoginResponseDto(accessToken, refreshToken, new UserResponseDto(user));
    }
}