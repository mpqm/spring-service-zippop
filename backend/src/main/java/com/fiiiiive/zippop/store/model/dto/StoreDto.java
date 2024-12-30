package com.fiiiiive.zippop.store.model.dto;

import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.global.common.constants.BaseStatus;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.dto.GoodsDto;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.entity.StoreLike;
import com.fiiiiive.zippop.store.model.entity.StoreReview;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class StoreDto {

    // 스토어 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateStoreReq {
        @NotBlank(message = "스토어 이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "스토어 이름은 최대 100자까지 입력 가능합니다.")
        private String storeName;

        @NotBlank(message = "스토어 주소는 필수 입력 항목입니다.")
        @Size(max = 200, message = "스토어 주소는 최대 200자까지 입력 가능합니다.")
        private String storeAddress;

        @NotBlank(message = "스토어 설명은 필수 입력 항목입니다.")
        @Size(max = 500, message = "스토어 설명은 최대 500자까지 입력 가능합니다.")
        private String storeContent;

        @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
        @Size(max = 50, message = "카테고리는 최대 50자까지 입력 가능합니다.")
        private String category;

        @NotNull(message = "총 인원수는 필수 입력 항목입니다.")
        @Min(value = 1, message = "총 인원수는 최소 1명 이상이어야 합니다.")
        private Integer totalPeople;

        @NotBlank(message = "스토어 시작 날짜는 필수 입력 항목입니다.")
        private String storeStartDate;

        @NotBlank(message = "스토어 종료 날짜는 필수 입력 항목입니다.")
        private String storeEndDate;

        public Store toEntity(CustomUserDetails customUserDetails, Company company){
            return Store.builder()
                    .companyEmail(customUserDetails.getEmail())
                    .name(this.getStoreName())
                    .content(this.getStoreContent())
                    .address(this.getStoreAddress())
                    .category(this.getCategory())
                    .totalPeople(this.getTotalPeople())
                    .startDate(this.getStoreStartDate())
                    .endDate(this.getStoreEndDate())
                    .likeCount(0)
                    .status(BaseStatus.STORE_START)
                    .company(company)
                    .build();
        }
    }

    // 스토어 이미지 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateStoreImageReq {
        // 상태 의존성 없음 유틸리티 함수 처럼 사용
        public static StoreImage toEntity(Store store, String url) {
            return StoreImage.builder()
                    .url(url)
                    .store(store)
                    .build();
        }
    }


    // 스토어 생성 응답 DTO
    @Getter
    @Builder
    public static class CreateStoreRes {
        private Long storeIdx;
    }

    // 스토어 리뷰 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateStoreReviewReq {
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

        public StoreReview toEntity(String email, Store store, Orders orders){
            return StoreReview.builder()
                    .customerEmail(email)
                    .customerName(orders.getCustomer().getName())
                    .title(this.getReviewTitle())
                    .content(this.getReviewContent())
                    .rating(this.getReviewRating())
                    .store(store)
                    .customer(orders.getCustomer())
                    .build();
        }
    }

    // 스토어 리뷰 생성 응답 DTO
    @Getter
    @Builder
    public static class CreateStoreReviewRes {
        private Long reviewIdx;
    }

    // 스토어 이미지 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchStoreImageRes {
        private Long storeImageIdx;
        private String storeImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // 스토어 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchStoreRes {
        private Long storeIdx;
        private String companyEmail;
        private String storeName;
        private String storeContent;
        private String storeAddress;
        private String category;
        private Integer likeCount;
        private Integer totalPeople;
        private String storeStartDate;
        private String storeEndDate;
        private String storeStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<SearchStoreReviewRes> searchStoreReviewResList;
        private List<GoodsDto.SearchGoodsRes> searchGoodsResList;
        private List<StoreDto.SearchStoreImageRes> searchStoreImageResList;
    }

    @Getter
    @Builder
    public static class CreateStoreLikeReq {
        public static StoreLike toEntity(Store store, Customer customer) {
            return StoreLike.builder()
                    .store(store)
                    .customer(customer)
                    .build();
        }
    }

    // 스토어 좋아요 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchStoreLikeRes {
        private Long storeIdx;
        private String companyEmail;
        private String storeName;
        private String storeContent;
        private String storeAddress;
        private String category;
        private Integer likeCount;
        private Integer totalPeople;
        private String storeStartDate;
        private String storeEndDate;
        private String storeStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<SearchStoreReviewRes> searchStoreReviewResList;
        private List<GoodsDto.SearchGoodsRes> searchGoodsResList;
        private List<StoreDto.SearchStoreImageRes> searchStoreImageResList;
    }

    // 스토어 리뷰 응답 DTO
    @Getter
    @Builder
    public static class SearchStoreReviewRes {
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

    // 스토어 수정 요청 DTO
    @Getter
    @Builder
    public static class UpdateStoreReq {
        @NotBlank(message = "스토어 이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "스토어 이름은 최대 100자까지 입력 가능합니다.")
        private String storeName;

        @NotBlank(message = "스토어 주소는 필수 입력 항목입니다.")
        @Size(max = 200, message = "스토어 주소는 최대 200자까지 입력 가능합니다.")
        private String storeAddress;

        @NotBlank(message = "스토어 설명은 필수 입력 항목입니다.")
        @Size(max = 500, message = "스토어 설명은 최대 500자까지 입력 가능합니다.")
        private String storeContent;

        @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
        @Size(max = 50, message = "카테고리는 최대 50자까지 입력 가능합니다.")
        private String category;

        @NotNull(message = "총 인원수는 필수 입력 항목입니다.")
        @Min(value = 1, message = "총 인원수는 최소 1명 이상이어야 합니다.")
        private Integer totalPeople;

        @NotBlank(message = "스토어 시작 날짜는 필수 입력 항목입니다.")
        private String storeStartDate;

        @NotBlank(message = "스토어 종료 날짜는 필수 입력 항목입니다.")
        private String storeEndDate;
    }

    // 스토어 수정 응답 DTO
    @Getter
    @Builder
    public static class UpdateStoreRes {
        private Long storeIdx;
    }

}
