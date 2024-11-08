package com.fiiiiive.zippop.reserve.model.dto;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReserveRes {
    private Long reserveIdx;
    private String onDoingUUID;
    private String waitingUUID;
    private Long storeIdx;
}
