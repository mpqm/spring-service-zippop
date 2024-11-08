package com.fiiiiive.zippop.review.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRes {
    private Long reviewIdx;
    private String customerEmail;
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SearchReviewImageRes> searchReviewImageResList = new ArrayList<>();
}
