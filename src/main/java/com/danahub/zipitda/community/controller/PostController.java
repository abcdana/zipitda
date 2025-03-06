package com.danahub.zipitda.community.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.community.dto.PostRequestDto;
import com.danahub.zipitda.community.dto.PostResponseDto;
import com.danahub.zipitda.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회 API", description = "게시글 ID로 게시글을 조회합니다.")
    public CommonResponse<PostResponseDto> getPost(@PathVariable Long id) {
        return CommonResponse.success(postService.getPost(id));
    }

    @GetMapping
    @Operation(summary = "전체 게시글 조회 API", description = "모든 게시글을 조회합니다.")
    public CommonResponse<List<PostResponseDto>> getAllPosts() {
        return CommonResponse.success(postService.getAllPosts());
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정 API", description = "게시글을 수정합니다.")
    public CommonResponse<Void> updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        postService.updatePost(id, requestDto);
        return CommonResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제합니다.")
    public CommonResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return CommonResponse.success();
    }
}