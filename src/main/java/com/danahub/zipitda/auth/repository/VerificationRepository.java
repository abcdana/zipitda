package com.danahub.zipitda.auth.repository;

import com.danahub.zipitda.auth.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Integer> {
    Optional<Verification> findFirstByTypeAndEmail(String type, String email); // 이메일로 검색
    Optional<Verification> findFirstByTypeAndMobile(String type, String mobile); // 휴대폰 번호로 검색
}