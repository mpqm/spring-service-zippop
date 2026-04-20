package com.fiiiiive.zippop.orders.model.dto;

import lombok.Builder;
import lombok.Getter;

// 주문 검증 요청 DTO
@Getter
@Builder
public class CreateOrdersReq {
    String impUid;
    Long popupIdx;
    Long reserveIdx;
}