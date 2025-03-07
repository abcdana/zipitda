package com.danahub.zipitda.community.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    @Column(nullable = false)
    private Long userId; // 좋아요 누른 사용자 ID

    @Column(nullable = false)
    private String targetType; // 좋아요 대상 타입 ("POST", "PRODUCT", "REVIEW")

    @Column(nullable = false)
    private Long targetId; // 좋아요가 속한 대상 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 좋아요 누른 시간
}