package com.fiiiiive.zippop.popup.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

// 팝업 리뷰 생성 요청 DTO
@Getter
@Builder
public class CreatePopupReviewReq {
    @NotBlank(message = "리뷰 제목은 필수 입력 항목입니다.")
    @Size(max = 100, message = "리뷰 제목은 최대 100자까지 입력 가능합니다.")
    private String reviewTitle;

    @NotBlank(message = "리뷰 내용은 필수 입력 항목입니다.")
    @Size(max = 1000, message = "리뷰 내용은 최대 1000자까지 입력 가능합니다.")
    private String reviewContent;

    @NotNull(message = "리뷰 평점은 필수 입력 항목입니다.")
    @Min(value = 1, message = "리뷰 평점은 최소 1 이상이어야 합니다.")
    @Max(value = 5, message = "리뷰 평점은 최대 5 이하여야 합니다.")
    private Integer reviewRating;


}
