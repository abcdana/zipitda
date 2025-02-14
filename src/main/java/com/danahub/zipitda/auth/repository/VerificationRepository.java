package com.danahub.zipitda.auth.repository;

import com.danahub.zipitda.auth.domain.Verification;
import com.danahub.zipitda.auth.domain.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Integer> {

    Optional<Verification> findFirstByTypeAndEmail(VerificationType type, String email);

    Optional<Verification> findFirstByTypeAndMobile(VerificationType type, String mobile);
}