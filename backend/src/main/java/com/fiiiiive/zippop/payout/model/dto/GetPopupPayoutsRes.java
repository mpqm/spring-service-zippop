package com.fiiiiive.zippop.payout.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetPopupPayoutsRes {
    Integer revenue;
    LocalDate payoutDate;
}
