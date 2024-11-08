package com.fiiiiive.zippop.review.model.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewReq {
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
}
