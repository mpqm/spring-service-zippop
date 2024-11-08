package com.fiiiiive.zippop.goods.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGoodsReq {
    private String productName;
    private Integer productPrice;
    private Integer productAmount;
    private String productContent;
}