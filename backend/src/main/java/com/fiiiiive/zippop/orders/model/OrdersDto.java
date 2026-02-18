package com.fiiiiive.zippop.orders.model;

import com.fiiiiive.zippop.goods.model.GoodsDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersDto {

    // 주문 상세 조회 응답 DTO
    @Getter
    @Builder
    public static class GetOrdersDetailRes {
        private Long ordersDetailIdx;
        private Integer eachPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private GoodsDto.GetGoodsRes getGoodsRes;
    }

    // 주문 조회 응답 DTO
    @Getter
    @Builder
    public static class GetOrdersRes {
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

    // 주문 검증 요청 DTO
    @Getter
    @Builder
    public static class CreateOrdersReq {
        String impUid;
        Long popupIdx;
        Long reserveIdx;
    }

    // 주문 검증 응답 DTO
    @Getter
    @Builder
    public static class CreateOrdersRes {
        private Long ordersIdx;
    }

    // 주문 확정 요청 DTO
    @Getter
    @Builder
    public static class UpdateOrdersReq {
        private Long popupIdx;
        private String status;
    }

    // 주문 확정 응답 DTO
    @Getter
    @Builder
    public static class UpdateOrdersRes {
        private Long ordersIdx;
    }

}
