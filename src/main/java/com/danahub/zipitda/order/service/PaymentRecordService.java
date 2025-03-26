package com.danahub.zipitda.order.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.order.domain.Payment;
import com.danahub.zipitda.order.domain.PaymentStatus;
import com.danahub.zipitda.order.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentRecordService {

    private final PaymentRepository paymentRepository;

    /**
     * 결제 실패 기록을 남기는 메서드
     * - Propagation.REQUIRES_NEW: 메인 트랜잭션과 분리해서 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failPaymentInNewTx(Long paymentId, String transactionId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.PAYMENT_NOT_FOUND));

        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailedReason(reason);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }
}