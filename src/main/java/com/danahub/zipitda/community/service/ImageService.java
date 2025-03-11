package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.Image;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.domain.TargetType;
import com.danahub.zipitda.community.dto.ImageUploadRequestDto;
import com.danahub.zipitda.community.repository.ImageRepository;
import com.danahub.zipitda.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final StorageService storageService;

    public String uploadImage(ImageUploadRequestDto requestDto) {

        log.debug("업로드 요청: targetType={}, targetId={}, file={}",
                requestDto.targetType(), requestDto.targetId(), requestDto.file().getOriginalFilename());

        String imageUrl = storageService.uploadFile(requestDto.file());

        Image image = Image.builder()
                .targetType(requestDto.targetType())
                .targetId(requestDto.targetId())
                .imageUrl(imageUrl)
                .build();

        imageRepository.save(image);
        return imageUrl;
    }

    // 이미지 업로드 후 암호화된 URL 반환
    public String uploadImage(MultipartFile file) {
        log.debug("이미지 업로드 요청: {}", file.getOriginalFilename());

        String encryptedUrl = storageService.uploadFile(file);

        Image image = Image.builder()
                .targetType(null)  // 일단 targetId, targetType null로 설정
                .targetId(null)
                .imageUrl(encryptedUrl)
                .build();

        imageRepository.save(image);
        return encryptedUrl;
    }

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

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        // 권한 체크: 요청한 userId와 게시글 작성자의 userId 비교
        Post post = postRepository.findById(image.getTargetId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        if (!post.getUserId().equals(imageId)) {
            throw new ZipitdaException(ErrorType.ACCESS_DENIED, Map.of("message", "해당 이미지를 삭제할 권한이 없습니다."));
        }

        storageService.deleteFile(image.getImageUrl());
        imageRepository.delete(image);
    }
}