package com.danahub.zipitda.auth.service;

import com.danahub.zipitda.auth.domain.Verification;
import com.danahub.zipitda.auth.dto.VerificationResponseDto;
import com.danahub.zipitda.auth.dto.VerificationSendCodeRequestDto;
import com.danahub.zipitda.auth.dto.VerificationVerifyCodeRequestDto;
import com.danahub.zipitda.auth.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;

    // 인증 코드 발송
    public VerificationResponseDto sendCode(VerificationSendCodeRequestDto request) {
        validateRequest(request);

        // 인증 코드 생성 및 저장
        String code = generateCode();
        Verification verification = Verification.builder()
                .type(request.getType())
                .email("email".equals(request.getType()) ? request.getRecipient() : null)
                .mobile("sms".equals(request.getType()) ? request.getRecipient() : null)
                .code(code)
                .retryCount(0) // 명시적으로 설정
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .isVerified(false)
                .build();

        verificationRepository.save(verification);

        // DTO 변환 및 반환
        return VerificationResponseDto.builder()
                .success(true)
                .message("인증 코드가 성공적으로 발송되었습니다.")
                .retryCount(verification.getRetryCount())
                .expiredAt(verification.getExpiredAt().toString())
                .build();
    }

    // 인증 코드 검증

    public VerificationResponseDto verifyCode(VerificationVerifyCodeRequestDto request) {
        Optional<Verification> optionalVerification;

        if ("email".equals(request.getType())) {
            optionalVerification = verificationRepository.findFirstByTypeAndEmail(request.getType(), request.getRecipient());
        } else if ("sms".equals(request.getType())) {
            optionalVerification = verificationRepository.findFirstByTypeAndMobile(request.getType(), request.getRecipient());
        } else {
            throw new IllegalArgumentException("잘못된 인증 타입입니다.");
        }

        Verification verification = optionalVerification.orElseThrow(() ->
                new IllegalArgumentException("인증 요청이 존재하지 않습니다.")
        );

        if (verification.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        if (!verification.getCode().equals(request.getCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        verification.setIsVerified(true);
        verificationRepository.save(verification);

        return VerificationResponseDto.builder()
                .success(true)
                .message("인증이 성공적으로 완료되었습니다.")
                .retryCount(verification.getRetryCount())
                .expiredAt(verification.getExpiredAt().toString())
                .build();
    }

    // 요청 유효성 검증
    private void validateRequest(VerificationSendCodeRequestDto request) {
        if ("email".equals(request.getType()) && !isValidEmail(request.getRecipient())) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        } else if ("sms".equals(request.getType()) && !isValidPhoneNumber(request.getRecipient())) {
            throw new IllegalArgumentException("유효하지 않은 전화번호 형식입니다.");
        }
    }

    // 인증 코드 생성
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    // 이메일 유효성 검사
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    // 전화번호 유효성 검사
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{10,11}");
    }
}