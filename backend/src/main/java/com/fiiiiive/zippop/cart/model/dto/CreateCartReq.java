package com.fiiiiive.zippop.cart.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartReq {
    private Long goodsIdx;
    private Long storeIdx;
}
