package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCartItemRes {
    private Integer count;
    private Integer price;
    private GetGoodsRes getGoodsRes;
}
