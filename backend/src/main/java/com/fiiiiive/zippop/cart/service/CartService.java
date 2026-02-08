package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.cart.model.Cart;
import com.fiiiiive.zippop.cart.model.CartDto;
import com.fiiiiive.zippop.cart.model.CartItem;
import com.fiiiiive.zippop.cart.policy.CartPolicy;
import com.fiiiiive.zippop.cart.repository.CartItemRepository;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.goods.model.Goods;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final CartPolicy cartPolicy;

    // 장바구니 등록
    @Transactional
    public void createCart(CustomUserDetails user, CartDto.CreateCartReq req) throws BaseException {

        // 고객 회원(customerIdx) 조회
        Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.CART_REGISTER_FAIL_MEMBER_NOT_FOUND)
        );

        // 굿즈(goodsIdx, popupIdx) 조회
        Goods goods = goodsRepository.findByGoodsIdxAndPopupIdx(req.getGoodsIdx(), req.getPopupIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.CART_REGISTER_FAIL_GOODS_NOT_FOUND)
        );

        // 장바구니 조회 후 없으면 장바구니 생성
        Cart cart = cartRepository.findByCustomerIdxAndPopupIdx(user.getIdx(), req.getPopupIdx()).orElseGet(
                () -> cartRepository.save(req.toEntity(customer, goods.getPopup()))
        );

        cart.validateNoDuplicateGoods(goods);

        cartItemRepository.save(CartDto.CreateCartItemReq.toEntity(cart, goods));

    }

    // 장바구니 목록 조회
    @Transactional(readOnly = true)
    public Page<CartDto.GetCartRes> getCarts(CustomUserDetails user, Integer page, Integer size) throws BaseException {

        // 장바구니 조회(customerIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Cart> cartPage = cartRepository.findAllByCustomerIdx(user.getIdx(), pageable).orElseThrow(
                () -> new BaseException(BaseMessage.CART_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Cart.toDtoPage(cartPage);

    }

    // 장바구니 전체 삭제
    @Transactional(rollbackFor = Exception.class)
    public void deleteCart(CustomUserDetails user, Long cartIdx) throws BaseException {

        // 장바구니 조회(cartIdx)
        Cart cart = cartRepository.findByCartIdx(cartIdx).orElseThrow(
                () -> new BaseException(BaseMessage.CART_DELETE_ALL_FAIL_NOT_FOUND)
        );

        // 장바구니 소유권확인
        cartPolicy.validateCartOwner(cart, user);

        cartRepository.delete(cart);

    }

    // 장바구니 아이템 목록 조회
    @Transactional(readOnly = true)
    public List<CartDto.GetCartItemRes> getCartItems(CustomUserDetails user, Long cartIdx) throws BaseException {

        // 장바구니 조회(cartIdx)
        Cart cart = cartRepository.findByCartIdx(cartIdx).orElseThrow(
                () -> new BaseException(BaseMessage.CART_DELETE_ALL_FAIL_NOT_FOUND)
        );

        // 장바구니 소유권확인
        cartPolicy.validateCartOwner(cart, user);

        return CartItem.toDtoList(cart.getCartItemList());

    }

    // 장바구니 아이템 수량 조절
    @Transactional
    public void updateCartItemQuantity(CustomUserDetails user, Long cartItemIdx, CartDto.UpdateCartItemQuantityReq req) throws BaseException {

        // 장바구니 아이템 조회 (소유자 확인 포함 - 한 번의 쿼리로 처리)
        CartItem cartItem = cartItemRepository.findByCartItemIdxAndCustomerIdx(cartItemIdx, user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.CART_ITEM_COUNT_FAIL_NOT_FOUND)
        );

        // Dirty Checking (operation = false -> +, operation = true -> --
        if (!req.getOperation()){
            cartItem.increase();
        } else {
            cartItem.decrease();
        }

    }

    // 장바구니 아이템 삭제
    @Transactional
    public void deleteCartItem(CustomUserDetails user, Long cartItemIdx) {

        // 장바구니 아이템 조회 (소유자 확인 포함 - 한 번의 쿼리로 처리)
        CartItem cartItem = cartItemRepository.findByCartItemIdxAndCustomerIdx(cartItemIdx, user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.CART_ITEM_COUNT_FAIL_NOT_FOUND)
        );

        // 장바구니 소유권확인
        cartPolicy.validateCartItemOwner(cartItem, user);

        // 장바구니 아이템 삭제(cartItemIdx, customerIdx)
        cartItemRepository.delete(cartItem);

    }

}


