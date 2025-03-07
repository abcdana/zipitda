package com.danahub.zipitda.community.dto;

import jakarta.validation.constraints.*;

public record PostRequestDto(

        Long postId,

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotBlank(message = "제목을 입력해야 합니다.")
        @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
        String title,

        @NotBlank(message = "내용을 입력해야 합니다.")
        @Size(max = 2000, message = "내용은 최대 2000자까지 입력 가능합니다.")
        String content
) { }