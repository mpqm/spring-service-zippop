package com.fiiiiive.zippop.store.model.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreReviewReq {
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
}
