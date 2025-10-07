package com.fiiiiive.zippop.settlement.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class SettlementDto {

    @Getter
    @Builder
    public static class SearchSettlementRes {
        Integer totalRevenue;
        LocalDate settlementDate;
    }
}
