package com.danahub.zipitda.auth.controller;

import com.danahub.zipitda.auth.dto.LoginResponseDto;
import com.danahub.zipitda.auth.dto.SocialLoginResponseDto;
import com.danahub.zipitda.auth.service.SocialLoginService;
import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/social-login")
@RequiredArgsConstructor
public class SocialLoginController {

    private final SocialLoginService socialLoginService;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;

    @GetMapping("/page")
    public String loginPage(Model model) {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri;

        String naverLoginUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="
                + naverClientId + "&redirect_uri=" + naverRedirectUri + "&state=STATE_STRING";

        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        model.addAttribute("naverLoginUrl", naverLoginUrl);

        return "login";
    }

    // 카카오 로그인 콜백
    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) {
        SocialLoginResponseDto socialLoginInfo = socialLoginService.processKakaoLogin(code);

        session.setAttribute("user", socialLoginInfo.user()); // 세션에 사용자 정보 저장

        return "redirect:/"; // 로그인 성공 후 index 페이지로 이동
    }

    // 네이버 로그인 콜백
    @GetMapping("/naver/callback")
    public CommonResponse<SocialLoginResponseDto> naverCallback(@RequestParam("code") String code) {
        return CommonResponse.success(socialLoginService.processNaverLogin(code));
    }

}