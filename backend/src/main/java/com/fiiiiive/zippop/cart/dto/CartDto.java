package com.fiiiiive.zippop.cart.dto;

import com.fiiiiive.zippop.auth.entity.Customer;
import com.fiiiiive.zippop.cart.entity.Cart;
import com.fiiiiive.zippop.cart.entity.CartItem;
import com.fiiiiive.zippop.goods.dto.GoodsDto;
import com.fiiiiive.zippop.goods.entity.Goods;
import com.fiiiiive.zippop.store.dto.StoreDto;
import com.fiiiiive.zippop.store.entity.Store;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CartDto {

    // 장바구니 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateCartReq {
        @NotNull(message = "상품 ID는 필수 입력 항목입니다.")
        private Long goodsIdx;

        @NotNull(message = "스토어 ID는 필수 입력 항목입니다.")
        private Long storeIdx;

        public Cart toEntity(Customer customer, Store store) {
            return Cart.builder()
                    .customer(customer)
                    .store(store)
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
                    .count(1)
                    .price(goods.getPrice())
                    .build();
        }
    }

    // 장바구니 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchCartRes {
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
        private List<StoreDto.SearchStoreImageRes> searchStoreImageResList;
    }

    // 장바구니 아이템 조회 응답 DTO
    @Getter
    @Builder
    public static class SearchCartItemRes {
        private Long cartItemIdx;
        private Integer count;
        private Integer price;
        private GoodsDto.SearchGoodsRes searchGoodsRes;
    }

}
