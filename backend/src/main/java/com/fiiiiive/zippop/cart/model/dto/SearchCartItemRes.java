package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCartItemRes {
    private Integer count;
    private Integer price;
    private SearchGoodsRes searchGoodsRes;
}
