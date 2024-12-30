package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.cart.model.dto.CartDto;
import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.cart.repository.CartItemRepository;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니 등록
    @Transactional
    public void registerCart(CustomUserDetails customUserDetails, CartDto.CreateCartReq dto) throws BaseException {

        // 고객 회원(customerIdx) 조회
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_MEMBER_NOT_FOUND)
        );

        // 굿즈(goodsIdx, storeIdx) 조회
        Goods goods = goodsRepository.findByGoodsIdxAndStoreIdx(dto.getGoodsIdx(), dto.getStoreIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_GOODS_NOT_FOUND)
        );

        // 장바구니 조회 후 없으면 장바구니 생성
        Cart cart = cartRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), dto.getStoreIdx()).orElseGet(
                () -> cartRepository.save(dto.toEntity(customer, goods.getStore()))
        );

        // 장바구니 아이템이 있으면 예외 없으면 생성
        if (cartItemRepository.findByGoodsIdxAndCartIdx(goods.getIdx(), cart.getIdx()).isPresent()) {
            throw new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_ITEM_EXIST);
        }
        cartItemRepository.save(CartDto.CreateCartItemReq.toEntity(cart, goods));

    }

    // 장바구니 목록 조회
    public Page<CartDto.SearchCartRes> searchAllCart(CustomUserDetails customUserDetails, Integer page, Integer size) throws BaseException {

        // 장바구니 조회(customerIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Cart> cartPage = cartRepository.findAllByCustomerIdx(customUserDetails.getIdx(), pageable).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // DTO 반환
        return Cart.toDtoPage(cartPage);

    }

    // 장바구니 아이템 목록 조회
    public List<CartDto.SearchCartItemRes> searchAllCartItem(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {

        // 장바구니 조회(customerIdx, storeIdx)
        Cart cart = cartRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_ITEM_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // DTO 반환
        return CartItem.toDtoList(cart.getCartItemList());

    }

    // 장바구니 아이템 수량 조절
    @Transactional
    public void countCartItem(CustomUserDetails customUserDetails, Long cartItemIdx, Boolean operation) throws BaseException {

        // 장바구니 아이템 조회(cartItemIdx, customerIdx)
        CartItem cartItem = cartItemRepository.findByCartItemIdxAndCustomerIdx(cartItemIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_ITEM_COUNT_FAIL_NOT_FOUND)
        );

        // if: operation = false -> 장바구니 아이템 증가
        // else: operation = true or cartItem.getCount() <= 0 -> 장바구니 아이템 감소
        if (!operation){
            cartItemRepository.incrementCount(cartItemIdx);
        } else {
            if (cartItem.getCount() <= 0) throw new BaseException(BaseResponseMessage.CART_ITEM_COUNT_FAIL_IS_ZERO);
            cartItemRepository.decrementCount(cartItemIdx);
        }

    }

    // 장바구니 아이템 삭제
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(CustomUserDetails customUserDetails, Long cartItemIdx) {

        // 장바구니 아이템 삭제(cartItemIdx, customerIdx)
        cartItemRepository.deleteByCartItemIdxAndCustomerIdx(cartItemIdx, customUserDetails.getIdx());

    }

    // 장바구니 아이템 전체 삭제
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllCartItem(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {

        // 장바구니 삭제(customerIdx, storeIdx)
        Cart cart = cartRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.CART_DELETE_ALL_FAIL_NOT_FOUND)
        );
        cartRepository.delete(cart);

    }

}


