package com.fiiiiive.zippop.popup_review.model.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePopupReviewReq {
    private String reviewTitle;
    private String reviewContent;
    private Integer rating;
}