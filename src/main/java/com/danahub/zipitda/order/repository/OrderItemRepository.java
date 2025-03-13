package com.danahub.zipitda.order.repository;

import com.danahub.zipitda.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
