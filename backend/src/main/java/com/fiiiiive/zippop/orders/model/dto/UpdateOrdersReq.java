package com.fiiiiive.zippop.orders.model.dto;

import lombok.Builder;
import lombok.Getter;

// 주문 확정 요청 DTO
@Getter
@Builder
public class UpdateOrdersReq {
    private Long popupIdx;
    private String status;
}