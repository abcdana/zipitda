package com.danahub.zipitda.order.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.common.security.CustomUserDetails;
import com.danahub.zipitda.order.dto.DirectOrderRequestDto;
import com.danahub.zipitda.order.dto.OrderRequestDto;
import com.danahub.zipitda.order.dto.PaymentRequestDtoForPG;
import com.danahub.zipitda.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order/orders")
@RequiredArgsConstructor
@Tag(name = "order", description = "주문 정보 API")
public class OrderController {

    private final OrderService orderService;



    // 장바구니 주문
    @PostMapping("/from-cart")
    @Operation(summary = "장바구니 주문", description = "장바구니 선택 상품을 주문합니다.")
    public CommonResponse<PaymentRequestDtoForPG> createCartOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody @Valid OrderRequestDto requestDto) {
        return CommonResponse.success(orderService.createOrderFromCart(userDetails, requestDto.shippingInfo(), requestDto.paymentInfo()));
    }

    // 단일 상품 주문
    @PostMapping("/direct")
    @Operation(summary = "단일 상품 주문", description = "상품 상세에서 바로 주문합니다.")
    public CommonResponse<PaymentRequestDtoForPG> createDirectOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestBody @Valid DirectOrderRequestDto requestDto) {
        return CommonResponse.success(orderService.createDirectOrder(userDetails, requestDto));
    }

}
