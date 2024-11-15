package com.fiiiiive.zippop.store.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchStoreReviewRes {
    private Long reviewIdx;
    private String storeName;
    private String customerEmail;
    private String customerName;
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
