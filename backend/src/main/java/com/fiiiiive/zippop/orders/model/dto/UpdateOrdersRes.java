package com.fiiiiive.zippop.orders.model.dto;

import lombok.Builder;
import lombok.Getter;

// 주문 확정 응답 DTO
@Getter
@Builder
public class UpdateOrdersRes {
    private Long ordersIdx;
}
