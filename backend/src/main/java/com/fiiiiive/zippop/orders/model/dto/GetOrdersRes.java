package com.fiiiiive.zippop.orders.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 주문 조회 응답 DTO
@Getter
@Builder
public class GetOrdersRes {
    private Long ordersIdx;
    private String impUid;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private Integer usedPoint;
    private Integer totalPrice;
    private String orderStatus;
    private Integer deliveryCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetOrdersDetailRes> getOrdersDetailResList;
}