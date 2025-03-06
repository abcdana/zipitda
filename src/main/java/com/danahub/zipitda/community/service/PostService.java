package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.dto.PostRequestDto;
import com.danahub.zipitda.community.dto.PostResponseDto;
import com.danahub.zipitda.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 생성
    @Transactional
    public Long createPost(PostRequestDto requestDto) {
        Post post = Post.builder()
                .userId(requestDto.userId())
                .title(requestDto.title())
                .content(requestDto.content())
                .build();
        return postRepository.save(post).getId();
    }

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));
        return new PostResponseDto(
                post.getId(), post.getUserId(), post.getTitle(),
                post.getContent(), post.getCreatedAt(), post.getUpdatedAt()
        );
    }

    // 전체 게시글 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostResponseDto(
                                post.getId(),
                                post.getUserId(),
                                post.getTitle(),
                                post.getContent(),
                                post.getCreatedAt(),
                                post.getUpdatedAt()
                            )).toList();
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long id, PostRequestDto requestDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));
        post.setTitle(requestDto.title());
        post.setContent(requestDto.content());
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND);
        }
        postRepository.deleteById(id);
    }
}