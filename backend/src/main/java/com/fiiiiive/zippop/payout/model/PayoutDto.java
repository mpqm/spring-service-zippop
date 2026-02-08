package com.fiiiiive.zippop.payout.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class PayoutDto {

    @Getter
    @Builder
    public static class SearchPayoutRes {
        Integer revenue;
        LocalDate payoutDate;
    }
}
