package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.common.security.CustomUserDetails;
import com.danahub.zipitda.community.domain.Image;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.domain.TargetType;
import com.danahub.zipitda.community.dto.ImageUploadRequestDto;
import com.danahub.zipitda.community.repository.ImageRepository;
import com.danahub.zipitda.community.repository.PostRepository;
import com.danahub.zipitda.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final StorageService storageService;
    private final UserService userService;

    // 포스트 등록 시 해당 포스트의 이미지 targetInfo 업데이트
    public void updateImageTargetInfo(Long postId, List<String> imageUrls, TargetType targetType) {
        List<Image> images = imageRepository.findByImageUrlIn(imageUrls);

        if (images.isEmpty()) {
            log.warn("업데이트할 이미지가 없습니다. postId={}, imageUrls={}", postId, imageUrls);
            return;
        }

        images.forEach(image -> {
            image.setTargetId(postId);
            image.setTargetType(targetType);
        });

        imageRepository.saveAll(images);
        log.info("이미지 targetId 및 targetType 업데이트 완료: postId={}, targetType={}", postId, targetType);
    }

    public String uploadImage(MultipartFile file, CustomUserDetails user) {
        log.debug("이미지 업로드 요청: {}", file.getOriginalFilename());

        String encryptedUrl = storageService.uploadFile(file);

        Image image = Image.builder()
                .userId(user.getUserId())
                .targetType(null)
                .targetId(null)
                .imageUrl(encryptedUrl)
                .build();

        imageRepository.save(image);
        return encryptedUrl;
    }

    public void deleteImage(Long imageId, CustomUserDetails user) {

        log.info("삭제 요청한 사용자 userId: {}", user.getUserId());
        log.info("삭제 대상 imageId: {}", imageId);

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        log.info("조회된 이미지 userId: {}", image.getUserId());

        if (!image.getUserId().equals(user.getUserId())) {
            throw new ZipitdaException(ErrorType.ACCESS_DENIED, Map.of("userId", user.getUserId()));
        }

        storageService.deleteFile(image.getImageUrl());
        imageRepository.delete(image);
        log.info("이미지 삭제 완료: imageId={}, userId={}", imageId, user.getUserId());
    }

    public void deleteImageByUrl(String encryptedUrl, CustomUserDetails user) {

        log.info("삭제 요청한 사용자 userId: {}", user.getUserId());
        log.info("삭제 대상 imageUrl: {}", encryptedUrl);

        Image image = imageRepository.findByImageUrl(encryptedUrl)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        log.info("조회된 이미지 userId: {}", image.getUserId());

        if (!image.getUserId().equals(user.getUserId())) {
            throw new ZipitdaException(ErrorType.ACCESS_DENIED, Map.of("userId", user.getUserId()));
        }

        storageService.deleteFile(image.getImageUrl());
        imageRepository.delete(image);
        log.info("이미지 삭제 완료: imageUrl={}, userId={}", encryptedUrl, user.getUserId());
    }

}