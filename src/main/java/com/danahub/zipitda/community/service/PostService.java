package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.aop.PostAuthorizationCheck;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    @PostAuthorizationCheck
    public void updatePost(PostRequestDto requestDto) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        post.setTitle(requestDto.title());
        post.setContent(requestDto.content());
    }

    // 게시글 삭제
    @PostAuthorizationCheck
    public void deletePost(PostRequestDto requestDto) {
        postRepository.deleteById(requestDto.postId());
    }

    /*
    // 게시글 검증
    private void validatePostRequest(PostRequestDto requestDto) {
        if (requestDto.title() == null || requestDto.title().isBlank()) {
            throw new ZipitdaException(ErrorType.MISSING_REQUIRED_VALUE);
        }
        if (requestDto.content() == null || requestDto.content().isBlank()) {
            throw new ZipitdaException(ErrorType.MISSING_REQUIRED_VALUE);
        }
    }*/
}