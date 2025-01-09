package com.danahub.zipitda.auth.domain;

import com.danahub.zipitda.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.ibatis.annotations.Case;

import java.time.LocalDateTime;

@Entity
@Table(name = "VERIFICATIONS")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Verification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK

    @Column(nullable = false)
    private String type; // 인증 유형 (email/sms)

    private String email; // 인증 email

    private String mobile; // 인증 폰 번호

    @Column(nullable = false)
    private String code; // 인증 코드

    @Column(nullable = false)
    private Boolean isVerified = false; // 인증 완료 여부

    @Column(nullable = false)
    private Integer retryCount = 0; // 재시도 횟수

    @Column(nullable = false)
    private LocalDateTime expiredAt; // 만료시간

    public Verification() {
    }
}