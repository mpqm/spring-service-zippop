package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.popup.model.dto.GetPopupImageRes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 장바구니 조회 응답 DTO
@Getter
@Builder
public class GetCartRes {
    private Long cartIdx;
    private Long popupIdx;
    private String companyEmail;
    private String popupName;
    private String popupContent;
    private String popupAddress;
    private String category;
    private Integer likeCount;
    private Integer totalPeople;
    private String popupStatus;
    private LocalDate popupStartDate;
    private LocalDate popupEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetPopupImageRes> getPopupImageResList;
}
