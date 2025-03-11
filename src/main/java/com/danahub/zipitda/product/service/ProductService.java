package com.danahub.zipitda.product.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.TargetType;
import com.danahub.zipitda.community.repository.ImageRepository;
import com.danahub.zipitda.community.service.ImageService;
import com.danahub.zipitda.product.domain.Product;
import com.danahub.zipitda.product.dto.ProductRequestDto;
import com.danahub.zipitda.product.dto.ProductResponseDto;
import com.danahub.zipitda.product.dto.ProductDetailResponseDto;
import com.danahub.zipitda.product.repository.ProductRepository;
import com.danahub.zipitda.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final UserService userService;

    // 상품 등록
    public Long createProduct(ProductRequestDto requestDto, Authentication authentication) {
        Long userId = userService.findUserIdByEmail(authentication.getName());

        Product product = Product.builder()
                .userId(userId)
                .name(requestDto.name())
                .description(requestDto.description())
                .category(requestDto.category())
                .price(requestDto.price())
                .stockQuantity(requestDto.stockQuantity())
                .build();

        Long productId = productRepository.save(product).getId();

        // 이미지 저장
        if (requestDto.imageUrls() != null && !requestDto.imageUrls().isEmpty()) {
            imageService.updateImageTargetInfo(productId, requestDto.imageUrls(), TargetType.PRODUCT);
        }

        return productId;
    }

    // 상품 목록 조회 (페이징)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> new ProductResponseDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getCreatedAt()
                ));
    }

    // 상품 상세 조회
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        // 이미지 조회
        List<String> imageUrls = imageRepository.findByTargetId(productId)
                .stream()
                .map(image -> image.getImageUrl())
                .toList();

        return new ProductDetailResponseDto(
                product.getId(),
                product.getUserId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                imageUrls
        );
    }

    // 상품 수정
    public void updateProduct(Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setCategory(requestDto.category());
        product.setPrice(requestDto.price());
        product.setStockQuantity(requestDto.stockQuantity());

        productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}