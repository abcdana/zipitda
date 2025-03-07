package com.danahub.zipitda.community.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.Post;
import com.danahub.zipitda.community.dto.PostDetailResponseDto;
import com.danahub.zipitda.community.dto.PostRequestDto;
import com.danahub.zipitda.community.dto.PostResponseDto;
import com.danahub.zipitda.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시글 API")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 등록 API", description = "새로운 게시글을 등록합니다.")
    public CommonResponse<Long> createPost(@RequestBody PostRequestDto requestDto) {
        return CommonResponse.success(postService.createPost(requestDto));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회 API", description = "게시글의 상세 정보를 조회합니다. (제목, 내용, 이미지, 작성자, 댓글 수, 좋아요 수, 북마크 수 포함)")
    public CommonResponse<PostDetailResponseDto> getPostDetail(@PathVariable Long postId) {
        return CommonResponse.success(postService.getPostDetail(postId));
    }

    @GetMapping
    @Operation(summary = "전체 게시글 조회 API", description = "페이지네이션을 적용하여 최신순으로 정렬된 게시글을 조회합니다. 좋아요순으로 정렬 가능.")
    public CommonResponse<Page<PostResponseDto>> getAllPosts(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "latest") String sortBy) {
        return CommonResponse.success(postService.getAllPosts(pageable, sortBy));
    }

    @PutMapping
    @Operation(summary = "게시글 수정 API", description = "게시글을 수정합니다.")
    public CommonResponse<Void> updatePost(@RequestBody PostRequestDto requestDto) {
        postService.updatePost(requestDto);
        return CommonResponse.success();
    }

    @DeleteMapping
    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제합니다.")
    public CommonResponse<Void> deletePost(@RequestBody PostRequestDto requestDto) {
        postService.deletePost(requestDto);
        return CommonResponse.success();
    }
}