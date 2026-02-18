package com.fiiiiive.zippop.goods.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class GoodsDto {

    // 굿즈 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateGoodsReq {

        @NotBlank(message = "팝업 아이디는 필수 입력 항목입니다.")
        private Long popupIdx;

        @NotBlank(message = "굿즈 이름은 필수 입력 항목입니다.")
        private String goodsName;

        @NotNull(message = "굿즈 가격은 필수 입력 항목입니다.")
        @Positive(message = "굿즈 가격은 양수여야 합니다.")
        @Min(value = 1, message = "굿즈 가격은 최소 1원 이상이어야 합니다.")
        private Integer goodsPrice;

        @NotNull(message = "굿즈 수량은 필수 입력 항목입니다.")
        @Positive(message = "굿즈 수량은 양수여야 합니다.")
        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
        @Max(value = 10000, message = "수량은 최대 10000개 이하여야 합니다.")
        private Integer goodsAmount;

        private String goodsContent;

    }

    // 굿즈 업데이트 요청 DTO
    @Getter
    @Builder
    public static class UpdateGoodsReq {
        @NotBlank(message = "굿즈 이름은 필수 입력 항목입니다.")
        private String goodsName;

        @NotNull(message = "굿즈 가격은 필수 입력 항목입니다.")
        @Positive(message = "굿즈 가격은 양수여야 합니다.")
        @Min(value = 1, message = "굿즈 가격은 최소 1원 이상이어야 합니다.")
        private Integer goodsPrice;

        @NotNull(message = "굿즈 수량은 필수 입력 항목입니다.")
        @Positive(message = "굿즈 수량은 양수여야 합니다.")
        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
        @Max(value = 10000, message = "수량은 최대 10000개 이하여야 합니다.")
        private Integer goodsAmount;

        private String goodsContent;
    }

    // 굿즈 이미지 조회 응답 DTO
    @Getter
    @Builder
    public static class GetGoodsImageRes {
        private Long goodsImageIdx;
        private String goodsImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // 굿즈 조회 응답 DTO
    @Getter
    @Builder
    public static class GetGoodsRes {
        private String popupName;
        private Long goodsIdx;
        private String goodsName;
        private Integer goodsPrice;
        private String goodsContent;
        private Integer goodsAmount;
        private String goodsStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<GetGoodsImageRes> getGoodsImageResList;
    }

}
