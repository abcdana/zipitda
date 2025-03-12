package com.danahub.zipitda.product.domain;

import com.danahub.zipitda.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private String category;  // 카테고리
    private Long price;  // 상품 가격
    private Long stockQuantity;  // 재고 수량

}