package com.fiiiiive.zippop.orders.model.dto;

import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchOrdersDetailRes {
    private Long ordersDetailIdx;
    private Integer eachPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SearchGoodsRes searchGoodsRes;

}
