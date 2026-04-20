package com.fiiiiive.zippop.cart.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

// 장바구니 생성 요청 DTO
@Getter
@Builder
public class CreateCartReq {
    @NotNull(message = "상품 ID는 필수 입력 항목입니다.")
    private Long goodsIdx;

    @NotNull(message = "팝업 ID는 필수 입력 항목입니다.")
    private Long popupIdx;

}