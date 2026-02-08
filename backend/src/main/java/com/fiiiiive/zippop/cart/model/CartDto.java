package com.fiiiiive.zippop.cart.model;

import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.goods.model.GoodsDto;
import com.fiiiiive.zippop.goods.model.Goods;
import com.fiiiiive.zippop.popup.model.PopupDto;
import com.fiiiiive.zippop.popup.model.Popup;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

        public Cart toEntity(Customer customer, Popup popup) {
            return Cart.builder()
                    .customer(customer)
                    .popup(popup)
                    .build();
        }
    }

    // 장바구니 아이템 생성 요청 DTO
    public static class CreateCartItemReq {
        // 상태 의존성 없음 유틸리티 함수 처럼 사용
        public static CartItem toEntity(Cart cart, Goods goods) {
            return CartItem.builder()
                    .cart(cart)
                    .goods(goods)
                    .quantity(1)
                    .price(goods.getPrice())
                    .build();
        }
    }

    // 장바구니 아이템 수량 수정 요청 DTO
    @Getter
    @Setter
    public static class UpdateCartItemQuantityReq {
        private Boolean operation;
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
        private String popupStartDate;
        private String popupEndDate;
        private String popupStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<PopupDto.SearchPopupImageRes> searchPopupImageResList;
    }

    // 장바구니 아이템 조회 응답 DTO
    @Getter
    @Builder
    public static class GetCartItemRes {
        private Long cartItemIdx;
        private Integer count;
        private Integer price;
        private GoodsDto.SearchGoodsRes searchGoodsRes;
    }

}
