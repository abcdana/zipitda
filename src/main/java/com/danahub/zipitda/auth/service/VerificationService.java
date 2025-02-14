package com.danahub.zipitda.auth.service;

import com.danahub.zipitda.auth.domain.Verification;
import com.danahub.zipitda.auth.domain.VerificationType;
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
        VerificationType verificationType = request.type();
        validateRequest(verificationType, request.recipient());

        // 인증 코드 생성 및 저장
        String code = generateCode();
        Verification verification = Verification.builder()
                .type(verificationType)
                .email(verificationType == VerificationType.EMAIL ? request.recipient() : null)
                .mobile(verificationType == VerificationType.SMS ? request.recipient() : null)
                .code(code)
                .retryCount(0)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .isVerified(false)
                .build();

        verificationRepository.save(verification);

        return new VerificationResponseDto(
                true,
                "인증 코드가 성공적으로 발송되었습니다.",
                verification.getRetryCount(),
                verification.getExpiredAt().toString()
        );
    }

    // 인증 코드 검증
    public VerificationResponseDto verifyCode(VerificationVerifyCodeRequestDto request) {
        VerificationType verificationType = request.type();

        Optional<Verification> optionalVerification =
                verificationType == VerificationType.EMAIL
                        ? verificationRepository.findFirstByTypeAndEmail(verificationType, request.recipient())
                        : verificationRepository.findFirstByTypeAndMobile(verificationType, request.recipient());

        Verification verification = optionalVerification.orElseThrow(
                () -> new IllegalArgumentException("인증 요청이 존재하지 않습니다.")
        );

        if (verification.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        if (!verification.getCode().equals(request.code())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        verification.setIsVerified(true);
        verificationRepository.save(verification);

        return new VerificationResponseDto(
                true,
                "인증이 성공적으로 완료되었습니다.",
                verification.getRetryCount(),
                verification.getExpiredAt().toString()
        );
    }

    // 요청 유효성 검증 (VerificationType 사용)
    private void validateRequest(VerificationType type, String recipient) {
        if (type == VerificationType.EMAIL && !isValidEmail(recipient)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        } else if (type == VerificationType.SMS && !isValidPhoneNumber(recipient)) {
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