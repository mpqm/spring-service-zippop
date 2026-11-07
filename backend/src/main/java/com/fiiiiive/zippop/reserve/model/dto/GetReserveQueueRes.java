package com.fiiiiive.zippop.reserve.model.dto;

import lombok.Builder;
import lombok.Getter;

// 예약큐 상태 응답 DTO
@Getter
@Builder
public class GetReserveQueueRes {
    private Long reserveIdx;
    private String waitingTotal;
    private String workingTotal;
    private String statusMessage;
    private Integer access;
    private String wtoken; // 토큰 추가
    private Long accessExpiresAt;

    public static GetReserveQueueRes toData(Long reserveIdx, String workingTotal, String waitingTotal, String statusMessage, Integer access) {
        return GetReserveQueueRes.builder()
                .reserveIdx(reserveIdx)
                .workingTotal(workingTotal)
                .waitingTotal(waitingTotal)
                .statusMessage(statusMessage)
                .access(access)
                .build();
    }

    public static GetReserveQueueRes toDataWithToken(Long reserveIdx, String workingTotal, String waitingTotal,
                                                     String statusMessage, Integer access, String wtoken,
                                                     Long accessExpiresAt) {
        return GetReserveQueueRes.builder()
                .reserveIdx(reserveIdx)
                .workingTotal(workingTotal)
                .waitingTotal(waitingTotal)
                .statusMessage(statusMessage)
                .access(access)
                .wtoken(wtoken)
                .accessExpiresAt(accessExpiresAt)
                .build();
    }
}
