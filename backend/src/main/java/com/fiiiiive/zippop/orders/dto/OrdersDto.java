package com.fiiiiive.zippop.orders.dto;

import com.fiiiiive.zippop.auth.entity.Customer;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.goods.dto.GoodsDto;
import com.fiiiiive.zippop.goods.entity.Goods;
import com.fiiiiive.zippop.orders.entity.Orders;
import com.fiiiiive.zippop.orders.entity.OrdersDetail;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersDto {

    // 주문 상세 목록 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchAllOrderDetailRes {
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
        private List<SearchOrdersDetailRes> searchOrdersDetailResList;
    }

    // 예약 굿즈 주문 저장 요청 DTO
    public static class CreateReserveOrdersReq {
        // 상태 의존성 없음 유틸리티 함수 처럼 사용
        public static Orders toEntity(String impUid, Integer totalPurchasePrice, Customer customer, Long storeIdx) {
            return Orders.builder()
                    .impUid(impUid)
                    .totalPrice(totalPurchasePrice)
                    .status(BaseStatus.RESERVE_READY)
                    .deliveryCost(0)
                    .usedPoint(0)
                    .customer(customer)
                    .storeIdx(storeIdx)
                    .build();
        }
    }

    // 재고 굿즈 주문 저장 요청 DTO
    public static class CreateStockOrdersReq {
        // 상태 의존성 없음 유틸리티 함수 처럼 사용
        public static Orders toEntity(String impUid, Integer totalPurchasePrice, Integer usedPoint, Customer customer, Long storeIdx) {
            return Orders.builder()
                    .impUid(impUid)
                    .totalPrice(totalPurchasePrice)
                    .status(BaseStatus.STOCK_READY)
                    .deliveryCost(2500)
                    .usedPoint(usedPoint)
                    .customer(customer)
                    .storeIdx(storeIdx)
                    .build();
        }
    }

    // 주문 상세 저장 요청 DTO
    public static class CreateOrdersDetailReq {
        // 상태 의존성 없음 유틸리티 함수 처럼 사용
        public static OrdersDetail toEntity(Orders orders, Goods goods, Integer eachPrice) {
            return OrdersDetail.builder()
                    .eachPrice(eachPrice)
                    .orders(orders)
                    .goods(goods)
                    .build();
        }
    }

    // 주문 상세 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchOrdersDetailRes {
        private Long ordersDetailIdx;
        private Integer eachPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private GoodsDto.SearchGoodsRes searchGoodsRes;
    }

    // 주문 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchOrdersRes {
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
        private List<OrdersDto.SearchOrdersDetailRes> searchOrdersDetailResList;
    }

    // 주문 검증 응답 DTO
    @Getter
    @Builder
    public static class VerifyOrdersRes {
        private Long ordersIdx;
    }
}
