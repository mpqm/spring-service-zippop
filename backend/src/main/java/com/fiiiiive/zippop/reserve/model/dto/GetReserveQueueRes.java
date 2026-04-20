package com.fiiiiive.zippop.reserve.model.dto;

import lombok.Builder;
import lombok.Getter;

// 예약큐 상태 응답 DTO
@Getter
@Builder
public class GetReserveQueueRes {
    private String waitingTotal;
    private String workingTotal;
    private String statusMessage;
    private Integer access;
    private String wtoken; // 토큰 추가

    public static GetReserveQueueRes toData(String workingTotal, String waitingTotal, String statusMessage, Integer access) {
        return GetReserveQueueRes.builder()
                .workingTotal(workingTotal)
                .waitingTotal(waitingTotal)
                .statusMessage(statusMessage)
                .access(access)
                .build();
    }

    public static GetReserveQueueRes toDataWithToken(String workingTotal, String waitingTotal, String statusMessage, Integer access, String wtoken) {
        return GetReserveQueueRes.builder()
                .workingTotal(workingTotal)
                .waitingTotal(waitingTotal)
                .statusMessage(statusMessage)
                .access(access)
                .wtoken(wtoken)
                .build();
    }
}