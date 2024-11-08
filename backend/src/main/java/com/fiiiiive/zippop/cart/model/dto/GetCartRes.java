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
    private Integer count;
    private Integer price;
    private GetGoodsRes getGoodsRes;
}
