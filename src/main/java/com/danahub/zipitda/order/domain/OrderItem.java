package com.danahub.zipitda.order.domain;

import com.danahub.zipitda.common.domain.BaseEntity;
import com.danahub.zipitda.store.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 상품 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;  // 주문 수량

    @Column(nullable = false)
    private BigDecimal price;  // 주문 시점의 단가

    @Column(name = "product_name_snapshot", nullable = false, length = 255)
    private String productNameSnapshot;  // 주문 시점의 상품명

    @Column(name = "option_info", length = 255)
    private String optionInfo;  // 옵션 정보 (예: 색상, 사이즈)

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;  // 썸네일 이미지 URL
}