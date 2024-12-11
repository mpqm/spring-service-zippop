package com.fiiiiive.zippop.settlement.model.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSettlementRes {
    Integer totalRevenue;
    LocalDate settlementDate;
}
