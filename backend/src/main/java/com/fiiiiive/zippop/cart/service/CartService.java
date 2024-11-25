package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.cart.model.dto.*;
import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.cart.repository.CartItemRepository;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final StoreRepository storeRepository;

    // 장바구니 등록
    @Transactional
    public void register(CustomUserDetails customUserDetails, CreateCartReq dto) throws BaseException {
        // 1. 예외 : 사용자가 없을때, 스토어 인덱스에 해당하는 스토어가 없을때, 스토어에 등록된 상품이 존재하지 않을 때
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_MEMBER_NOT_FOUND)
        );
        Store store = storeRepository.findByStoreIdx(dto.getStoreIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_STORE_NOT_FOUND)
        );
        Goods goods = goodsRepository.findByGoodsIdxAndStoreIdx(dto.getGoodsIdx(), store.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_GOODS_NOT_FOUND)
        );

        // 2. Cart 조회
        // if : 장바구니가 없으면 장바구니와 장바구니 아이템을 생성
        // else : 장바구니가 있으면 굿즈 인덱스에 해당하는 장바구니 아이템가 있으면 예외, 없으면 추가
        Optional<Cart> cartOpt = cartRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), dto.getStoreIdx());
        Cart cart;
        CartItem cartItem;
        if(cartOpt.isEmpty()){
            cart = Cart.builder()
                    .customer(customer)
                    .store(store)
                    .build();
            cartRepository.save(cart);
            cartItem = CartItem.builder()
                    .cart(cart)
                    .goods(goods)
                    .count(1)
                    .price(goods.getPrice())
                    .build();
            cartItemRepository.save(cartItem);
        } else {
            cart = cartOpt.get();
            Optional<CartItem> resultCartItem = cartItemRepository.findByGoodsIdxAndCartIdx(goods.getIdx(), cart.getIdx());
            if (resultCartItem.isPresent()) {
                throw new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_ITEM_EXIST);
            }
            cartItem = CartItem.builder()
                    .cart(cart)
                    .goods(goods)
                    .count(1)
                    .price(goods.getPrice())
                    .build();
            cartItemRepository.save(cartItem);
        }
    }

    // 장바구니 목록 조회
    public Page<SearchCartRes> searchAll(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        // 1. 예외: 사용자가 등록한 카드 목록을 찾지 못했을때
        Page<Cart> cartPage = cartRepository.findAllByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), PageRequest.of(page, size)).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // 2. Cart DTO Page 반환
        Page<SearchCartRes> searchCartResPage = cartPage.map(cart -> {
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
            for(StoreImage storeImage : cart.getStore().getStoreImageList()){
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getIdx())
                        .storeImageUrl(storeImage.getUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            return SearchCartRes.builder()
                    .storeIdx(cart.getStore().getIdx())
                    .companyEmail(cart.getStore().getCompanyEmail())
                    .storeName(cart.getStore().getName())
                    .storeContent(cart.getStore().getContent())
                    .storeAddress(cart.getStore().getAddress())
                    .category(cart.getStore().getCategory())
                    .likeCount(cart.getStore().getLikeCount())
                    .totalPeople(cart.getStore().getTotalPeople())
                    .storeStartDate(cart.getStore().getStartDate())
                    .storeEndDate(cart.getStore().getEndDate())
                    .createdAt(cart.getCreatedAt())
                    .updatedAt(cart.getUpdatedAt())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        return searchCartResPage;
    }
}


