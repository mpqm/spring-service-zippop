package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import lombok.Builder;
import lombok.Getter;

// 장바구니 아이템 조회 응답 DTO
@Getter
@Builder
public class GetCartItemRes {
    private Long cartItemIdx;
    private Integer count;
    private Integer price;
    private GetGoodsRes getGoodsRes;
}