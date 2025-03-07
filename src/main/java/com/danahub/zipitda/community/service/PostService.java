package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.aop.PostAuthorizationCheck;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.dto.PostDetailResponseDto;
import com.danahub.zipitda.community.dto.PostRequestDto;
import com.danahub.zipitda.community.dto.PostResponseDto;
import com.danahub.zipitda.community.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 생성
    @Transactional
    public Long createPost(@Valid PostRequestDto requestDto) {

        Post post = Post.builder()
                .userId(requestDto.userId())
                .title(requestDto.title())
                .content(requestDto.content())
                .build();
        return postRepository.save(post).getId();
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        int likeCount = postRepository.countLikesByPostId(postId);
        int bookmarkCount = postRepository.countBookmarksByPostId(postId);

        return new PostDetailResponseDto(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                likeCount,
                bookmarkCount
        );
    }

    // 전체 게시글 조회 (페이징 + 정렬)
    @Transactional(readOnly = true)
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
    @Transactional
    public void updatePost(PostRequestDto requestDto) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        post.setTitle(requestDto.title());
        post.setContent(requestDto.content());
    }

    // ✅ 게시글 삭제 (권한 체크는 AOP에서 처리)
    @PostAuthorizationCheck
    @Transactional
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