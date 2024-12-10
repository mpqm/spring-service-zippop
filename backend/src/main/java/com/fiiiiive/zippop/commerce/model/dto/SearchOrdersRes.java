package com.fiiiiive.zippop.commerce.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchOrdersRes {
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
