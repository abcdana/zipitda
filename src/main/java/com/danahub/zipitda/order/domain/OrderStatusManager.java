package com.danahub.zipitda.order.domain;

import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.common.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class OrderStatusManager {

    // 상태 전환 허용 맵
    private static final Map<OrderStatus, Set<OrderStatus>> allowedTransitions = Map.of(
            OrderStatus.CREATED, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCEL_REQUESTED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PACKAGING, OrderStatus.CANCEL_REQUESTED),
            OrderStatus.PACKAGING, EnumSet.of(OrderStatus.SHIPPED),
            OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, EnumSet.of(OrderStatus.RETURN_REQUESTED, OrderStatus.REFUND_REQUESTED),

            OrderStatus.CANCEL_REQUESTED, EnumSet.of(OrderStatus.CANCELED),
            OrderStatus.RETURN_REQUESTED, EnumSet.of(OrderStatus.RETURNED),
            OrderStatus.REFUND_REQUESTED, EnumSet.of(OrderStatus.REFUNDED)
    );

    // 상태 전환 유효성 검증 후 새 상태 반환
    public static OrderStatus transition(OrderStatus currentStatus, OrderStatus newStatus) {
        Set<OrderStatus> allowed = allowedTransitions.getOrDefault(currentStatus, EnumSet.noneOf(OrderStatus.class));
        if (!allowed.contains(newStatus)) {
            throw new ZipitdaException(ErrorType.INVALID_ORDER_STATUS_TRANSITION, Map.of("현재상태 : ", currentStatus, "/변경 상태 : ", newStatus));
        }
        return newStatus;
    }
}