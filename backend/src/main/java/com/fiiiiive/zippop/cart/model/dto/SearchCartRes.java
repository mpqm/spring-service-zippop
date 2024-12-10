package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCartRes {
    private Long storeIdx;
    private String companyEmail;
    private String storeName;
    private String storeContent;
    private String storeAddress;
    private String category;
    private Integer likeCount;
    private Integer totalPeople;
    private String storeStartDate;
    private String storeEndDate;
    private String storeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
}