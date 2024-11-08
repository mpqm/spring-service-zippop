package com.fiiiiive.zippop.popup_reserve.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePopupReserveReq {
    private Long storeIdx;
    private Long maxCount;
    private Long expireTime;
}
