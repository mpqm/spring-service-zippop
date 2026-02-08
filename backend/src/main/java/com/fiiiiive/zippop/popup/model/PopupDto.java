package com.fiiiiive.zippop.popup.model;

import com.fiiiiive.zippop.account.model.Company;
import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.global.enums.StoreStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.GoodsDto;
import com.fiiiiive.zippop.orders.model.Orders;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PopupDto {

    // 팝업 생성 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePopupReq {
        @NotBlank(message = "팝업 이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "팝업 이름은 최대 100자까지 입력 가능합니다.")
        private String popupName;

        @NotBlank(message = "팝업 주소는 필수 입력 항목입니다.")
        @Size(max = 200, message = "팝업 주소는 최대 200자까지 입력 가능합니다.")
        private String popupAddress;

        @NotBlank(message = "팝업 설명은 필수 입력 항목입니다.")
        @Size(max = 500, message = "팝업 설명은 최대 500자까지 입력 가능합니다.")
        private String popupContent;

        @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
        @Size(max = 50, message = "카테고리는 최대 50자까지 입력 가능합니다.")
        private String category;

        @NotNull(message = "총 인원수는 필수 입력 항목입니다.")
        @Min(value = 1, message = "총 인원수는 최소 1명 이상이어야 합니다.")
        private Integer totalPeople;

        @NotBlank(message = "팝업 시작 날짜는 필수 입력 항목입니다.")
        private LocalDate popupStartDate;

        @NotBlank(message = "팝업 종료 날짜는 필수 입력 항목입니다.")
        private LocalDate popupEndDate;


    }

    // 팝업 이미지 생성 요청 DTO
    @Getter
    @Builder
    public static class CreatePopupImageReq {
        // 상태 의존성 없음 유틸리티 함수 처럼 사용
        public static PopupImage toEntity(Popup popup, String url) {
            return PopupImage.builder()
                    .url(url)
                    .popup(popup)
                    .build();
        }
    }


    // 팝업 생성 응답 DTO
    @Getter
    @Builder
    public static class CreatePopupRes {
        private Long popupIdx;
    }

    // 팝업 리뷰 생성 요청 DTO
    @Getter
    @Builder
    public static class CreatePopupReviewReq {
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

    // 팝업 리뷰 생성 응답 DTO
    @Getter
    @Builder
    public static class CreatePopupReviewRes {
        private Long reviewIdx;
    }

    // 팝업 이미지 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchPopupImageRes {
        private Long popupImageIdx;
        private String popupImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // 팝업 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchPopupRes {
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
        private List<SearchPopupReviewRes> searchPopupReviewResList;
        private List<GoodsDto.SearchGoodsRes> searchGoodsResList;
        private List<PopupDto.SearchPopupImageRes> searchPopupImageResList;
    }

    @Getter
    @Builder
    public static class CreatePopupLikeReq {

    }

    // 팝업 좋아요 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchPopupLikeRes {
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
        private List<SearchPopupReviewRes> searchPopupReviewResList;
        private List<GoodsDto.SearchGoodsRes> searchGoodsResList;
        private List<PopupDto.SearchPopupImageRes> searchPopupImageResList;
    }

    // 팝업 리뷰 응답 DTO
    @Getter
    @Builder
    public static class SearchPopupReviewRes {
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

    // 팝업 수정 요청 DTO
    @Getter
    @Builder
    public static class UpdatePopupReq {
        @NotBlank(message = "팝업 이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "팝업 이름은 최대 100자까지 입력 가능합니다.")
        private String popupName;

        @NotBlank(message = "팝업 주소는 필수 입력 항목입니다.")
        @Size(max = 200, message = "팝업 주소는 최대 200자까지 입력 가능합니다.")
        private String popupAddress;

        @NotBlank(message = "팝업 설명은 필수 입력 항목입니다.")
        @Size(max = 500, message = "팝업 설명은 최대 500자까지 입력 가능합니다.")
        private String popupContent;

        @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
        @Size(max = 50, message = "카테고리는 최대 50자까지 입력 가능합니다.")
        private String category;

        @NotNull(message = "총 인원수는 필수 입력 항목입니다.")
        @Min(value = 1, message = "총 인원수는 최소 1명 이상이어야 합니다.")
        private Integer totalPeople;

        @NotBlank(message = "팝업 시작 날짜는 필수 입력 항목입니다.")
        private LocalDate popupStartDate;

        @NotBlank(message = "팝업 종료 날짜는 필수 입력 항목입니다.")
        private LocalDate popupEndDate;
    }

    // 팝업 수정 응답 DTO
    @Getter
    @Builder
    public static class UpdatePopupRes {
        private Long popupIdx;
    }

}
