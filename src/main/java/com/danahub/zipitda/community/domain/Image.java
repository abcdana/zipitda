package com.danahub.zipitda.community.domain;

import com.danahub.zipitda.common.domain.BaseEntity;
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
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이미지 ID

    @Enumerated(EnumType.STRING)
    private TargetType targetType; // POST / PRODUCT / REVIEW 등

    @Column(name = "targetId")
    private Long targetId; // 대상 ID

    @Column(nullable = false)
    private String imageUrl; // 이미지 URL

    @Column(nullable = false)
    private Long userId;  // 등록자 ID

    @Column(name = "thumbnail_yn", nullable = false)
    private boolean thumbnailYn;

    public void updateTargetInfo(Long targetId, TargetType targetType) {
        this.targetId = targetId;
        this.targetType = targetType;
    }
}