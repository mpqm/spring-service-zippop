package com.fiiiiive.zippop.popup.model.dto;

import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 팝업 조회 응답 DTO
@Getter
@Builder
public class GetPopupRes {
    private Long popupIdx;
    private String companyEmail;
    private String popupName;
    private String popupContent;
    private String popupAddress;
    private String category;
    private Integer likeCount;
    private Integer totalPeople;
    private LocalDate popupStartDate;
    private LocalDate popupEndDate;
    private String popupStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetPopupReviewRes> getPopupReviewResList;
    private List<GetGoodsRes> getGoodsResList;
    private List<GetPopupImageRes> getPopupImageResList;
}