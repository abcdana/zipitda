package com.danahub.zipitda.order.repository;

import com.danahub.zipitda.order.domain.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
}
