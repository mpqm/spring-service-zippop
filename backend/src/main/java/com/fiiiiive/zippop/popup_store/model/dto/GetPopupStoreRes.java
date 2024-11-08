package com.fiiiiive.zippop.popup_store.model.dto;


import com.fiiiiive.zippop.popup_goods.model.dto.GetPopupGoodsRes;
import com.fiiiiive.zippop.popup_review.model.dto.GetPopupReviewRes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPopupStoreRes {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetPopupReviewRes> getPopupReviewResList = new ArrayList<>();
    private List<GetPopupGoodsRes> getPopupGoodsResList = new ArrayList<>();
    private List<GetPopupStoreImageRes> getPopupStoreImageResList = new ArrayList<>();
}