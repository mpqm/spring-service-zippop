package com.fiiiiive.zippop.orders.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersRes {
    private Long customerOrdersIdx;
    private String impUid;
    private Integer usedPoint;
    private Integer totalPrice;
    private String orderState;
    private Integer deliveryCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrdersDetailRes> searchCustomerOrdersDetailResList = new ArrayList<>();
}
