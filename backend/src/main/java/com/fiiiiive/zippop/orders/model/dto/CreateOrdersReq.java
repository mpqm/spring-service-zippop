package com.fiiiiive.zippop.orders.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

// 주문 검증 요청 DTO
@Getter
@Builder
public class CreateOrdersReq {
    @NotBlank(message = "결제 식별자는 필수 입력 항목입니다.")
    String impUid;

    @NotNull(message = "팝업 ID는 필수 입력 항목입니다.")
    Long popupIdx;

    Long reserveIdx;
}
