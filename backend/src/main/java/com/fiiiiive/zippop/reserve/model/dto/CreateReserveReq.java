package com.fiiiiive.zippop.reserve.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReserveReq {
    private Long storeIdx;
    private Long maxCount;
    private Long expireTime;
}
