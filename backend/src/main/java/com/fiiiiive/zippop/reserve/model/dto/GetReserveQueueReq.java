package com.fiiiiive.zippop.reserve.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 예약 상태 요청 DTO
@Data
public class GetReserveQueueReq {
    @NotNull(message = "예약 ID는 필수 입력 항목입니다.")
    private Long reserveIdx;
}