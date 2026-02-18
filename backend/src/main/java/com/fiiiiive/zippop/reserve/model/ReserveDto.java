package com.fiiiiive.zippop.reserve.model;

import com.fiiiiive.zippop.popup.model.PopupDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReserveDto {

    // 예약 생성 요청 DTO
    @Getter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class CreateReserveReq {
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

    // 예약 생성 응답 DTO
    @Getter
    @Builder
    public static class CreateReserveRes {
        private Long reserveIdx;
    }

    // 예약 상태 요청 DTO
    @Data
    public static class StatusReserveReq {
        @NotNull(message = "예약 ID는 필수 입력 항목입니다.")
        private Long reserveIdx;
    }

    // 예약 상태 응답 DTO
    @Getter
    @Builder
    public static class StatusReserveRes {
        private String waitingTotal;
        private String workingTotal;
        private String statusMessage;
        private Integer access;
        private String wtoken; // 토큰 추가

        public static StatusReserveRes toData(String workingTotal, String waitingTotal, String statusMessage, Integer access) {
            return StatusReserveRes.builder()
                    .workingTotal(workingTotal)
                    .waitingTotal(waitingTotal)
                    .statusMessage(statusMessage)
                    .access(access)
                    .build();
        }

        public static StatusReserveRes toDataWithToken(String workingTotal, String waitingTotal, String statusMessage, Integer access, String wtoken) {
            return StatusReserveRes.builder()
                    .workingTotal(workingTotal)
                    .waitingTotal(waitingTotal)
                    .statusMessage(statusMessage)
                    .access(access)
                    .wtoken(wtoken)
                    .build();
        }
    }

    // 예약 조회 DTO
    @Getter
    @Builder
    public static class GetReserveRes {
        private Long popupIdx;
        private Long reserveIdx;
        private Integer reservePeople;
        private LocalDate reserveStartDate;
        private LocalDateTime reserveStartTime;
        private LocalDateTime reserveEndTime;
        private PopupDto.GetPopupRes getPopupRes;
    }

    // 예약 등록 응답 DTO
    @Getter
    @Builder
    public static class EnrollReserveRes {
        private String response;
    }
}
