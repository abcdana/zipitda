package com.danahub.zipitda.terms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

// 복합키를 정의하는 클래스
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TermsId implements Serializable {

    @Column(nullable = false)
    private String title; // 약관 제목

    @Column(nullable = false)
    private Integer version; // 약관 버전

}