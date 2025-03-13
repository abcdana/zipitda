package com.danahub.zipitda.order.domain;

public enum PaymentMethod {
    CARD,           // 카드 결제
    BANK_TRANSFER,  // 무통장입금
    KAKAO_PAY,      // 카카오페이
    NAVER_PAY,      // 네이버페이
    TOSS_PAY,       // 토스페이
    MOBILE_PAY      // 핸드폰 결제
}