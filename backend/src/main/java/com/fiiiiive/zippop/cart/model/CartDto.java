package com.fiiiiive.zippop.cart.model;

import com.fiiiiive.zippop.goods.model.GoodsDto;
import com.fiiiiive.zippop.popup.model.PopupDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CartDto {

    // 장바구니 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateCartReq {
        @NotNull(message = "상품 ID는 필수 입력 항목입니다.")
        private Long goodsIdx;

        @NotNull(message = "팝업 ID는 필수 입력 항목입니다.")
        private Long popupIdx;

    }

    // 장바구니 조회 응답 DTO
    @Getter
    @Builder
    public static class GetCartRes {
        private Long popupIdx;
        private String companyEmail;
        private String popupName;
        private String popupContent;
        private String popupAddress;
        private String category;
        private Integer likeCount;
        private Integer totalPeople;
        private String popupStatus;
        private LocalDate popupStartDate;
        private LocalDate popupEndDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<PopupDto.GetPopupImageRes> getPopupImageResList;
    }

    // 장바구니 아이템 조회 응답 DTO
    @Getter
    @Builder
    public static class GetCartItemRes {
        private Long cartItemIdx;
        private Integer count;
        private Integer price;
        private GoodsDto.GetGoodsRes getGoodsRes;
    }

}
