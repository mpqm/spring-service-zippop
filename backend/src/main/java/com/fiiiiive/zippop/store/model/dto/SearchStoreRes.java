package com.fiiiiive.zippop.store.model.dto;


import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchStoreRes {
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
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SearchStoreReviewRes> searchStoreReviewResList = new ArrayList<>();
    private List<SearchGoodsRes> searchGoodsResList = new ArrayList<>();
    private List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
}
