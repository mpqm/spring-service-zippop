package com.fiiiiive.zippop.orders.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatePaymentIntentReq {
    @NotNull(message = "팝업 ID는 필수 입력 항목입니다.")
    private Long popupIdx;

    private Long reserveIdx;

    @NotEmpty(message = "결제할 상품은 한 개 이상이어야 합니다.")
    @Valid
    private List<@NotNull PaymentIntentGoodsReq> goods;

    @NotNull
    @PositiveOrZero
    private Integer usedPoint;
}
