package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.cart.model.dto.SearchCartItemRes;
import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.cart.repository.CartItemRepository;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsImageRes;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니아이템 목록 조회
    public List<SearchCartItemRes> itemSearchAll(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        // 1. 예외 : 사용자, 스토어 인덱스로 장바구니를 찾지 못했을때
        Cart cart = cartRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_ITEM_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // 2. CartItem Dto List 반환
        List<SearchCartItemRes> searchCartItemResList = new ArrayList<>();
        for(CartItem cartItem: cart.getCartItemList()){
            Goods goods = cartItem.getGoods();
            List<SearchGoodsImageRes> searchGoodsImageResList = goods.getGoodsImageList().stream().map(image ->
                            SearchGoodsImageRes.builder()
                                    .goodsImageIdx(image.getIdx())
                                    .goodsImageUrl(image.getUrl())
                                    .createdAt(image.getCreatedAt())
                                    .updatedAt(image.getUpdatedAt())
                                    .build())
                    .collect(Collectors.toList());
            SearchGoodsRes searchGoodsRes = SearchGoodsRes.builder()
                    .goodsIdx(goods.getIdx())
                    .goodsName(goods.getName())
                    .goodsPrice(goods.getPrice())
                    .goodsContent(goods.getContent())
                    .goodsAmount(goods.getAmount())
                    .createdAt(goods.getCreatedAt())
                    .updatedAt(goods.getUpdatedAt())
                    .searchGoodsImageResList(searchGoodsImageResList)
                    .build();
            SearchCartItemRes searchCartItemRes = SearchCartItemRes.builder()
                    .cartItemIdx(cartItem.getIdx())
                    .count(cartItem.getCount())
                    .price(cartItem.getPrice())
                    .searchGoodsRes(searchGoodsRes)
                    .build();
            searchCartItemResList.add(searchCartItemRes);
        }
        return searchCartItemResList;
    }

    // 장바구니 아이템 수량 조절
    @Transactional
    public void itemCount(CustomUserDetails customUserDetails, Long cartItemIdx, Boolean flag) throws BaseException {
        // 1. 예외 : 사용자, 장바구니 아이템 인덱스로 조회한 장바구니아이템이 없을 때
        CartItem cartItem = cartItemRepository.findByCartItemIdxAndCustomerIdx(cartItemIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_ITEM_COUNT_FAIL_NOT_FOUND)
        );

        /*
        2. 현재 장바구니 아이템 개수를 조절
        if : flag = true 증가
        else : flag = false 감소 / 현재 값이 0 이면 예외
         */
        if (flag){
            cartItemRepository.incrementCount(cartItemIdx);
        } else {
            if (cartItem.getCount() <= 0) {
                throw new BaseException(BaseResponseMessage.CART_ITEM_COUNT_FAIL_IS_ZERO);
            }
            cartItemRepository.decrementCount(cartItemIdx);
        }
    }

    // 장바구니 아이템 삭제
    @Transactional(rollbackFor = Exception.class)
    public void itemDelete(Long cartItemIdx, CustomUserDetails customUserDetails) throws BaseException {
        // 1. 사용자, 장바구니 인덱스로 장바구니 아이템 삭제
        cartItemRepository.deleteByCartItemIdxAndCustomerIdx(cartItemIdx, customUserDetails.getIdx());
    }

    // 장바구니 아이템 전체 삭제
    @Transactional(rollbackFor = Exception.class)
    public void itemDeleteAll(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        // 2. 사용자, 스토어 인덱스로 장바구니 삭제
        Cart cart = cartRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_DELETE_ALL_FAIL_NOT_FOUND)
        );
        cartRepository.delete(cart);
    }
}
