package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.cart.model.dto.*;
import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.cart.repository.CartItemRepository;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsImageRes;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;

    // 카트 등록
    @Transactional
    public CreateCartRes register(CustomUserDetails customUserDetails, CreateCartReq dto) throws BaseException {
        // 예외: 사용자의 카트를 찾지 못했을때, 등록된 상품이 없을때,
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_MEMBER_NOT_FOUND));
        Goods goods = goodsRepository.findById(dto.getGoodsIdx())
        .orElseThrow(() -> (new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_GOODS_NOT_FOUND)));
        // Cart 조회 및 존재하지 않을 경우 새로 생성
        Cart cart = cartRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseGet(() -> {
            Cart newCart = Cart.builder().customer(customer).build();
            cartRepository.save(newCart);
            return newCart;
        });
        // 카트에 이미 동일한 상품이 있는지 확인
        boolean isExist = cart.getCartItemList().stream()
        .anyMatch(cartItem -> cartItem.getGoods().getIdx().equals(dto.getGoodsIdx()));
        if(isExist){
            throw new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_EXIST);
        }
        // CartItem 생성 후 카트에 추가
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .goods(goods)
                .count(dto.getCount())
                .price(goods.getPrice())
                .build();
        cartItemRepository.save(cartItem);

        // CreateCartRes 반환
        return CreateCartRes.builder().cartIdx(cart.getIdx()).cartItemIdx(cartItem.getIdx()).build();
    }

    // 카트아이템 목록 조회
    public SearchCartRes searchAll(CustomUserDetails customUserDetails) throws BaseException {
        // 예외: 카트를 찾지 못했을때
        Cart cart = cartRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.CART_SEARCH_FAIL));
        // CartItem Dto List 생성
        List<SearchCartItemRes> searchCartItemResList = new ArrayList<>();
        for(CartItem cartItem: cart.getCartItemList()){
            Goods goods = cartItem.getGoods();
            // Goods Imge Dto List 생성
            List<SearchGoodsImageRes> searchGoodsImageResList = goods.getGoodsImageList().stream().map(image ->
                    SearchGoodsImageRes.builder()
                        .goodsImageIdx(image.getIdx())
                        .goodsImageUrl(image.getUrl())
                        .createdAt(image.getCreatedAt())
                        .updatedAt(image.getUpdatedAt())
                    .build())
            .collect(Collectors.toList());
            // Goods Dto 생성
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
            // CartItem Dto 생성
            SearchCartItemRes searchCartItemRes = SearchCartItemRes.builder()
                    .count(cartItem.getCount())
                    .price(cartItem.getPrice())
                    .searchGoodsRes(searchGoodsRes)
                    .build();
            searchCartItemResList.add(searchCartItemRes);
        }
        // SearchCartRes 반환
        return SearchCartRes.builder()
                .cartIdx(cart.getIdx())
                .searchCartItemResList(searchCartItemResList)
                .build();
    }

    // 카트아이템 수량조절
    @Transactional
    public CountCartItemRes count(CustomUserDetails customUserDetails, Long cartItemIdx, Long operation) throws BaseException {
        CartItem cartItem = cartItemRepository.findByCartItemIdx(cartItemIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.CART_COUNT_FAIL_NOT_FOUND));
        Integer currentCount = cartItem.getCount();
        if (operation == 0){
            currentCount++;
            cartItemRepository.incrementCount(cartItemIdx);
        } else if (operation == 1) {
            if (currentCount <= 0) {
                throw new BaseException(BaseResponseMessage.CART_COUNT_FAIL_IS_0);
            }
            currentCount--;
            cartItemRepository.decrementCount(cartItemIdx);
        } else{
            throw new BaseException(BaseResponseMessage.CART_COUNT_FAIL_INVALID_OPERATION);
        }
        return CountCartItemRes.builder().cartItemIdx(cartItemIdx).count(currentCount).build();
    }

    // 카트아이템 삭제
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long cartItemIdx) throws BaseException {
        cartItemRepository.deleteByCartItemIdx(cartItemIdx);
    }

    // 카트삭제
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(CustomUserDetails customUserDetails) throws BaseException {
        cartRepository.deleteByCustomerIdx(customUserDetails.getIdx());
    }
}


