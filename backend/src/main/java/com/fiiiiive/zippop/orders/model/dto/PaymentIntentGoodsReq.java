package com.fiiiiive.zippop.orders.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentIntentGoodsReq {
    @NotNull
    private Long goodsIdx;

    @NotNull
    @Positive
    private Integer count;
}
