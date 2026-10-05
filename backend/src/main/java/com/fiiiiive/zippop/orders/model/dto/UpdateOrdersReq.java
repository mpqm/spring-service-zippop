package com.fiiiiive.zippop.orders.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

// 주문 확정 요청 DTO
@Getter
@Builder
public class UpdateOrdersReq {
    private Long popupIdx;

    @NotBlank(message = "주문 상태는 필수 입력 항목입니다.")
    private String status;
}
