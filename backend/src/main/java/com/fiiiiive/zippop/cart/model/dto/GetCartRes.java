package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCartRes {
    private Long cartIdx;
    private Integer itemCount;
    private Integer itemPrice;
    private GetGoodsRes getGoodsRes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
