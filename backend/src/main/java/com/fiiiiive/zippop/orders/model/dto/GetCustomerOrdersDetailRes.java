package com.fiiiiive.zippop.orders.model.dto;

import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCustomerOrdersDetailRes {
    private Long companyOrdersDetailIdx;
    private Integer eachPrice;
    private String trackingNumber;
    private SearchGoodsRes searchGoodsRes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
