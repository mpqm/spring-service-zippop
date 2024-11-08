package com.fiiiiive.zippop.orders.model.dto;

import com.fiiiiive.zippop.popup_store.model.dto.GetPopupStoreRes;
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
    private GetPopupStoreRes getPopupStoreRes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
