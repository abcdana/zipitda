package com.danahub.zipitda.order.domain;
public enum OrderStatus {
    CREATED,             // 주문 생성됨 (장바구니 → 결제 직전)
    CONFIRMED,           // 주문 확정됨 (결제 성공 후)
    PACKAGING,           // 상품 포장 준비 중
    SHIPPED,             // 배송 시작
    DELIVERED,           // 배송 완료

    CANCEL_REQUESTED,    // 주문 취소 요청
    CANCELED,            // 주문 취소 완료

    RETURN_REQUESTED,    // 반품 요청
    RETURNED,            // 반품 완료

    REFUND_REQUESTED,    // 환불 요청
    REFUNDED             // 환불 완료
}