package com.fiiiiive.zippop.reserve.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStatusRes {
    private String waitingTotal;
    private String workingTotal;
    private String statusMessage;
    private Boolean access;
}
