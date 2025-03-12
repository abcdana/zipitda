package com.danahub.zipitda.store.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.store.dto.ProductDetailResponseDto;
import com.danahub.zipitda.store.dto.ProductRequestDto;
import com.danahub.zipitda.store.dto.ProductResponseDto;
import com.danahub.zipitda.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "상품 관리 API") // 태그 설정
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "상품 등록 API", description = "새로운 상품을 등록합니다.")
    public CommonResponse<Long> createProduct(Authentication authentication, @RequestBody @Valid ProductRequestDto requestDto) {
        return CommonResponse.success(productService.createProduct(requestDto, authentication));
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회 API", description = "전체 상품을 조회합니다. 기본 정렬 : 최신순")
    public CommonResponse<Page<ProductResponseDto>> getAllProducts(Pageable pageable) {
        return CommonResponse.success(productService.getAllProducts(pageable));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회 API", description = "상품 상세 내역을 조회합니다.")
    public CommonResponse<ProductDetailResponseDto> getProductDetail(@PathVariable Long productId) {
        return CommonResponse.success(productService.getProductDetail(productId));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "상품 수정 API", description = "상품을 수정합니다.")
    public CommonResponse<Void> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDto requestDto) {
        productService.updateProduct(productId, requestDto);
        return CommonResponse.success();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "상품 삭제 API", description = "상품을 삭제합니다.")
    public CommonResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return CommonResponse.success();
    }
}