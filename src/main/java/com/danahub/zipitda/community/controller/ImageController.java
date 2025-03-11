package com.danahub.zipitda.community.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.community.dto.ImageUploadRequestDto;
import com.danahub.zipitda.community.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/community/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 관리 API")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @Operation(summary = "이미지 업로드 API", description = "이미지를 업로드하고 URL을 반환합니다.")
    public CommonResponse<String> uploadImage(@Valid @ModelAttribute ImageUploadRequestDto requestDto) {
        return CommonResponse.success(imageService.uploadImage(requestDto));
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "이미지 삭제 API", description = "사용자가 업로드한 이미지를 삭제합니다.")
    public CommonResponse<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return CommonResponse.success();
    }
}