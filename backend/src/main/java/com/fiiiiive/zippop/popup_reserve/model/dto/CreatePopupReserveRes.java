package com.fiiiiive.zippop.popup_reserve.model.dto;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePopupReserveRes {
    private Long reserveIdx;
    private String reserveUUID;
    private String reserveWaitingUUID;
    private Long storeIdx;
}
