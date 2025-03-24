package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.aop.PostAuthorizationCheck;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.common.security.CustomUserDetails;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.domain.TargetType;
import com.danahub.zipitda.community.domain.Image;
import com.danahub.zipitda.community.dto.PostDetailResponseDto;
import com.danahub.zipitda.community.dto.PostRequestDto;
import com.danahub.zipitda.community.dto.PostResponseDto;
import com.danahub.zipitda.community.repository.ImageRepository;
import com.danahub.zipitda.community.repository.PostRepository;
import com.danahub.zipitda.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    private final ImageService imageService;
    private final UserService userService;

    // 게시글 생성
    public Long createPost(@Valid PostRequestDto requestDto, Authentication authentication) {
        // JWT에서 사용자 이메일 추출
        String userEmail = authentication.getName();

        // 이메일을 기반으로 userId 조회 (유저 서비스 필요)
        Long userId = userService.findUserIdByEmail(userEmail);

        Post post = Post.builder()
                .userId(userId)
                .title(requestDto.title())
                .content(requestDto.content())
                .build();

        Long postId = postRepository.save(post).getId();

        // 등록된 postId를 이미지 targetId로 업데이트
        if (requestDto.imageUrls() != null && !requestDto.imageUrls().isEmpty()) {
            imageService.updateImageTargetInfo(postId, requestDto.imageUrls(), TargetType.POST);
        }

        return postId;
    }

    // 게시글 상세 조회
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        int likeCount = postRepository.countLikesByPostId(postId);
        int bookmarkCount = postRepository.countBookmarksByPostId(postId);

        // 해당 postId의 이미지 리스트 조회
        List<String> imageUrls = imageRepository.findByTargetId(postId)
                .stream()
                .map(Image::getImageUrl)
                .toList();

        return new PostDetailResponseDto(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                likeCount,
                bookmarkCount,
                imageUrls  // 이미지 리스트 추가
        );
    }

    // 전체 게시글 조회 (페이징 + 정렬)
    public Page<PostResponseDto> getAllPosts(Pageable pageable, String sortBy) {
        Sort sort = switch (sortBy) {
            case "likes" -> Sort.by(Sort.Order.desc("likeCount"));
            case "comments" -> Sort.by(Sort.Order.desc("commentCount"));
            default -> Sort.by(Sort.Order.desc("createdAt")); // 최신순 (기본값)
        };

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return postRepository.findAll(sortedPageable).map(
                post -> new PostResponseDto(
                        post.getId(),
                        post.getUserId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ));
    }

    // 게시글 수정
    public void updatePost(PostRequestDto requestDto, CustomUserDetails user) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));
        if (!post.getUserId().equals(user.getUserId())) {
            throw new ZipitdaException(ErrorType.ACCESS_DENIED);
        }

        post.setTitle(requestDto.title());
        post.setContent(requestDto.content());
        log.info("게시글 수정 완료 - postId: {}, userId: {}", post.getId(), user.getUserId());

    }

    // 게시글 삭제
    public void deletePost(Long postId, CustomUserDetails user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        if (!post.getUserId().equals(user.getUserId())) {
            throw new ZipitdaException(ErrorType.ACCESS_DENIED);
        }

        postRepository.delete(post);
        log.info("게시글 삭제 완료 - postId: {}, userId: {}", postId, user.getUserId());
    }
}
