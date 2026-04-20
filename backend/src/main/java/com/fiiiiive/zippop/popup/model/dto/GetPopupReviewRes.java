package com.fiiiiive.zippop.popup.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 팝업 리뷰 응답 DTO
@Getter
@Builder
public class GetPopupReviewRes {
    private Long reviewIdx;
    private String popupName;
    private String customerEmail;
    private String customerName;
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}