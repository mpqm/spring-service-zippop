package com.fiiiiive.zippop.reserve.model.dto;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReserveRes {
    private Long reserveIdx;
    private String reserveUUID;
    private String reserveWaitingUUID;
    private Long storeIdx;
}
