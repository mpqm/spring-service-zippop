package com.fiiiiive.zippop.orders.model.dto;

import lombok.Builder;
import lombok.Getter;

// 주문 검증 응답 DTO
@Getter
@Builder
public class CreateOrdersRes {
    private Long ordersIdx;
}