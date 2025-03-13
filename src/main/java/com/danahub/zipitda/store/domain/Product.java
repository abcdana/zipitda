package com.danahub.zipitda.store.domain;

import com.danahub.zipitda.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 상품 ID

    private Long userId;  // 등록한 사용자 ID
    private String name;  // 상품명
    private String description;  // 상품 설명
    private BigDecimal price;  // 상품 가격
    private int stockQuantity;  // 재고 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // 상품 카테고리
}