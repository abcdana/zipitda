package com.danahub.zipitda.community.dto;

import com.danahub.zipitda.community.domain.TargetType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequestDto(
        Long imageId,
        @NotNull(message = "사용자 ID가 필요합니다.")
        Long userId,

        @NotNull(message = "대상 ID가 필요합니다.")
        Long targetId,

        @NotNull(message = "대상 타입이 필요합니다.")
        TargetType targetType, // POST, PRODUCT, REVIEW 등 구분을 위한 필드 추가

        @NotNull(message = "이미지 파일이 필요합니다.")
        MultipartFile file
) {}