package com.fiiiiive.zippop.orders.model.dto;

import com.fiiiiive.zippop.store.model.dto.GetStoreRes;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCompanyOrdersDetailRes {
    private Long companyOrdersDetailIdx;
    private Integer totalPrice;
    private GetStoreRes getStoreRes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
