package com.danahub.zipitda.community.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 북마크 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TargetType targetType; // 대상 유형 ("POST", "PRODUCT", "REVIEW")

    @Column(nullable = false)
    private Long targetId; // 대상 ID

    @Column(nullable = false)
    private Long userId; // 북마크한 사용자 ID

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시
}