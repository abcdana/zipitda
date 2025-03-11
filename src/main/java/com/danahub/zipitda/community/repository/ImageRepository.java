package com.danahub.zipitda.community.repository;

import com.danahub.zipitda.community.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 여러 개의 이미지 URL을 기준으로 이미지 리스트 조회
    List<Image> findByImageUrlIn(List<String> imageUrls);

    // 특정 postId의 이미지 조회
    List<Image> findByTargetId(Long postId);

}
