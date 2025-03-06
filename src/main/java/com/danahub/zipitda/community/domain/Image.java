package com.danahub.zipitda.community.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이미지 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType; // POST / PRODUCT / REVIEW 등

    @Column(nullable = false)
    private Long targetId; // 대상 ID

    @Column(nullable = false)
    private String imageUrl; // 이미지 URL

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시
}