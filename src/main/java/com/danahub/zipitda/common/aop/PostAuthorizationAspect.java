package com.danahub.zipitda.common.aop;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.dto.PostRequestDto;
import com.danahub.zipitda.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 게시글 권한 체크 AOP (DTO 활용)
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostAuthorizationAspect {

    private final PostRepository postRepository;

    // 게시글 수정/삭제 요청 전에 권한을 체크하는 AOP
    @Before("@annotation(com.danahub.zipitda.common.aop.PostAuthorizationCheck)")
    public void checkPostAuthorization(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        PostRequestDto requestDto = null;
        for (Object arg : args) {
            if (arg instanceof PostRequestDto dto) {
                requestDto = dto;
                break;
            }
        }

        if (requestDto == null) {
            throw new ZipitdaException(ErrorType.MISSING_REQUIRED_VALUE);
        }

        Long postId = requestDto.postId(); // 게시글 ID
        Long userId = requestDto.userId(); // 요청한 사용자 ID

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        // 권한 체크 - 요청한 userId와 게시글 작성자의 userId 비교
        if (!post.getUserId().equals(userId)) {
            throw new ZipitdaException(ErrorType.ACCESS_DENIED);
        }

        log.info("게시글 권한 체크 완료 - postId: {}, userId: {}", postId, userId);
    }
}