package com.danahub.zipitda.terms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TermsResponseDto {
    private String title;
    private int version;
    private String content;
    private boolean isRequired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
