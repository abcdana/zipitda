package com.danahub.zipitda.order.dto;

import java.util.List;

public record CartListResponseDto(
        List<CartResponseDto> items
) { }