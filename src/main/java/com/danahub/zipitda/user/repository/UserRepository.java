package com.danahub.zipitda.user.repository;

import com.danahub.zipitda.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByNickname(String nickname); // 닉네임 중복 체크
    boolean existsByEmail(String email); // 이메일 중복 체크
    Optional<User> findByEmail(String email); // 이메일로 사용자 조회
}