package com.danahub.zipitda.terms.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms") // 약관 테이블
@Getter
@Setter
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 약관 ID

    @Column(nullable = false)
    private String title; // 약관 제목

    @Column(nullable = false)
    private String content; // 약관 내용

    @Column(nullable = false)
    private Integer version; // 약관 버전

    @Column(nullable = false)
    private Boolean isRequired; // 필수 약관 여부 (true: 필수, false: 선택)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일시
}