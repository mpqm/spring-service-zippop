package com.fiiiiive.zippop.domain.payout.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class PayoutDto {

    @Getter
    @Builder
    public static class SearchPayoutRes {
        Integer totalRevenue;
        LocalDate payoutDate;
    }
}
