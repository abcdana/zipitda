package com.danahub.zipitda.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // user ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (중복 불가)

    @Column(nullable = false)
    private String password; // 패스워드

    private String nickname; // 닉네임 (중복 불가)
    private String profileImage; // 프로필 이미지
    private Boolean isActive; // 활성 사용자 (true: 활성, false: 비활성)

    @Column(nullable = false)
    private String role; // 역할 (USER/ADMIN/SELLER/EXPERT)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일시

}