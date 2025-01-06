package com.danahub.zipitda.terms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

// 복합키를 정의하는 클래스
@Getter
@Setter
@Embeddable
public class TermsId implements Serializable {

    @Column(nullable = false)
    private String title; // 약관 제목

    @Column(nullable = false)
    private Integer version; // 약관 버전

    public TermsId(String title, Integer version) {
        this.title = title;
        this.version = version;
    }

    public TermsId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermsId termsId = (TermsId) o;
        return Objects.equals(title, termsId.title) && Objects.equals(version, termsId.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, version);
    }
}