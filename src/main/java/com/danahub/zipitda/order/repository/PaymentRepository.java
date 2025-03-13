package com.danahub.zipitda.order.repository;

import com.danahub.zipitda.order.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
