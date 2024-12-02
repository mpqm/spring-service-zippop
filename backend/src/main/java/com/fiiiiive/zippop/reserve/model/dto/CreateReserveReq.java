package com.fiiiiive.zippop.reserve.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReserveReq {
    private Long storeIdx;
    private Integer reservePeople;
    private LocalDate reserveStartDate;
    private LocalDateTime reserveStartTime;
    private LocalDateTime reserveEndTime;
}
