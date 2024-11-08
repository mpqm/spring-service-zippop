package com.fiiiiive.zippop.goods.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGoodsReq {
    private String productName;
    private Integer productPrice;
    private Integer productAmount;
    private String productContent;
}
