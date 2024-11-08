package com.fiiiiive.zippop.orders.model.dto;

import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCompanyOrdersDetailRes {
    private Long companyOrdersDetailIdx;
    private Integer totalPrice;
    private SearchStoreRes searchStoreRes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
