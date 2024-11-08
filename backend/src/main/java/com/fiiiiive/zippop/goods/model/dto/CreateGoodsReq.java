package com.fiiiiive.zippop.goods.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGoodsReq {
    private String goodsName;
    private Integer goodsPrice;
    private Integer goodsAmount;
    private String goodsContent;
}
