package com.danahub.zipitda.order.controller;

import com.danahub.zipitda.order.dto.PaymentCallbackRequestDto;
import com.danahub.zipitda.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentCallbackController {

    private final PaymentService paymentService;

    @PostMapping("/callback")
    public ResponseEntity<Void> handlePaymentCallback(@RequestBody PaymentCallbackRequestDto callbackDto) {
        paymentService.processPaymentCallback(callbackDto);
        return ResponseEntity.ok().build();
    }
}