package com.fiiiiive.zippop.orders.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePaymentIntentRes {
    private String merchantUid;
    private Integer amount;
}
