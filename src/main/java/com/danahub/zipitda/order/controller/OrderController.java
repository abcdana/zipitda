package com.danahub.zipitda.order.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.order.dto.OrderRequestDto;
import com.danahub.zipitda.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/order/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 정보 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public CommonResponse<Long> createOrder(@RequestBody @Valid OrderRequestDto requestDto, Authentication authentication){
        return CommonResponse.success(orderService.createOrder(authentication, requestDto.orderItems(), requestDto.shippingInfo(), requestDto.paymentInfo()));
    }

}
