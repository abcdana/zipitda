package com.danahub.zipitda.store.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.community.domain.TargetType;
import com.danahub.zipitda.community.repository.ImageRepository;
import com.danahub.zipitda.community.service.ImageService;
import com.danahub.zipitda.store.repository.CategoryRepository;
import com.danahub.zipitda.store.domain.Category;
import com.danahub.zipitda.store.repository.ProductRepository;
import com.danahub.zipitda.store.domain.Product;
import com.danahub.zipitda.store.dto.ProductDetailResponseDto;
import com.danahub.zipitda.store.dto.ProductRequestDto;
import com.danahub.zipitda.store.dto.ProductResponseDto;
import com.danahub.zipitda.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final UserService userService;

    // 상품 등록
    public Long createProduct(ProductRequestDto requestDto, Authentication authentication) {
        Long userId = userService.findUserIdByEmail(authentication.getName());

        // categoryId로 Category 객체 조회
        Category category = categoryRepository.findById(requestDto.categoryId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND, Map.of("categoryId", requestDto.categoryId())));


        Product product = Product.builder()
                .userId(userId)
                .name(requestDto.name())
                .description(requestDto.description())
                .category(category)
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
                        product.getCategory().getName(),
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
                product.getCategory().getName(),
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
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND,
                                                        Map.of("productId", productId, "productName", requestDto.name())));


        // categoryId로 Category 객체 조회
        Category category = categoryRepository.findById(requestDto.categoryId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND, Map.of("categoryId", requestDto.categoryId())));


        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setCategory(category);
        product.setPrice(requestDto.price());
        product.setStockQuantity(requestDto.stockQuantity());

        productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}