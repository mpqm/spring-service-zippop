package com.fiiiiive.zippop.store.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreReq {
    private String storeName;
    private String storeAddress;
    private String storeContent;
    private String category;
    private Integer totalPeople;
    private String storeStartDate;
    private String storeEndDate;
}
