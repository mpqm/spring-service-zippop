package com.fiiiiive.zippop.reserve.model.dto;

import com.fiiiiive.zippop.popup.model.dto.GetPopupRes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 예약 조회 DTO
@Getter
@Builder
public class GetReserveRes {
    private Long popupIdx;
    private Long reserveIdx;
    private Integer reservePeople;
    private LocalDate reserveStartDate;
    private LocalDateTime reserveStartTime;
    private LocalDateTime reserveEndTime;
    private GetPopupRes getPopupRes;
}
