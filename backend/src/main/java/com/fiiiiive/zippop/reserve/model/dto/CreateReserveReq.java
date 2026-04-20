package com.fiiiiive.zippop.reserve.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 예약 생성 요청 DTO
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateReserveReq {
    @NotNull(message = "팝업 ID는 필수 입력 항목입니다.")
    private Long popupIdx;

    @NotNull(message = "예약 인원 수는 필수 입력 항목입니다.")
    private Integer reservePeople;

    @NotNull(message = "예약 시작 날짜는 필수 입력 항목입니다.")
//        @FutureOrPresent(message = "예약 시작 날짜는 현재 또는 미래여야 합니다.")
    private LocalDate reserveStartDate;

    @NotNull(message = "예약 시작 시간은 필수 입력 항목입니다.")
//        @FutureOrPresent(message = "예약 시작 시간은 현재 또는 미래여야 합니다.")
    private LocalDateTime reserveStartTime;

    @NotNull(message = "예약 종료 시간은 필수 입력 항목입니다.")
    @Future(message = "예약 종료 시간은 미래여야 합니다.")
    private LocalDateTime reserveEndTime;
}