package com.fiiiiive.zippop.orders.model.dto;

import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 주문 상세 조회 응답 DTO
@Getter
@Builder
public class GetOrdersDetailRes {
    private Long ordersDetailIdx;
    private Integer eachPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private GetGoodsRes getGoodsRes;
}