package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCartItemRes {
    private Long cartItemIdx;
    private Integer count;
    private Integer price;
    private SearchGoodsRes searchGoodsRes;
}
