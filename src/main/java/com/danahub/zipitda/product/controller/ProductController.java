package com.danahub.zipitda.product.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.product.dto.ProductDetailResponseDto;
import com.danahub.zipitda.product.dto.ProductRequestDto;
import com.danahub.zipitda.product.dto.ProductResponseDto;
import com.danahub.zipitda.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public CommonResponse<Long> createProduct(
            Authentication authentication,
            @RequestBody @Valid ProductRequestDto requestDto) {
        return CommonResponse.success(productService.createProduct(requestDto, authentication));
    }

    @GetMapping
    public CommonResponse<Page<ProductResponseDto>> getAllProducts(Pageable pageable) {
        return CommonResponse.success(productService.getAllProducts(pageable));
    }

    @GetMapping("/{productId}")
    public CommonResponse<ProductDetailResponseDto> getProductDetail(@PathVariable Long productId) {
        return CommonResponse.success(productService.getProductDetail(productId));
    }

    @PutMapping("/{productId}")
    public CommonResponse<Void> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDto requestDto) {
        productService.updateProduct(productId, requestDto);
        return CommonResponse.success();
    }

    @DeleteMapping("/{productId}")
    public CommonResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return CommonResponse.success();
    }
}