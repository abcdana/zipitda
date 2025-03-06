package com.danahub.zipitda.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // user ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (중복 불가)

    @Column(nullable = true)
    private String password; // 패스워드

    @Column(nullable = false, unique = true)
    private String nickname; // 닉네임 (중복 불가)

    private String profileImage; // 프로필 이미지
    private String isActive; // 활성 사용자 (ACTIVE: 활성, INACTIVE: 비활성)

    @Column(nullable = false)
    private String role; // 역할 (USER/ADMIN/SELLER/EXPERT)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL; // 소셜 로그인 제공자 (KAKAO, NAVER, GOOGLE, LOCAL)

    @PrePersist
    public void prePersist() {
        if (this.provider == null) {
            this.provider = AuthProvider.LOCAL;
        }
    }

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일시
}