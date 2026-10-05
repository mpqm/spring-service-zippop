package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.cart.model.dto.CreateCartReq;
import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.cart.policy.CartPolicy;
import com.fiiiiive.zippop.cart.repository.CartItemRepository;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock GoodsRepository goodsRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock CartPolicy cartPolicy;

    private CartService cartService;
    private CustomUserDetails user;
    private Popup popup;
    private Goods goods;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, goodsRepository, cartItemRepository, cartPolicy);
        user = CustomUserDetails.builder().idx(3L).email("user@test.com").role("ROLE_CUSTOMER").build();
        popup = Popup.builder().idx(7L).build();
        goods = Goods.builder().idx(11L).popup(popup).name("굿즈").price(1000).amount(10).build();
    }

    @Test
    void rejectsDuplicateGoodsInAnExistingCart() {
        Cart cart = Cart.builder().idx(5L).customer(null).popup(popup).cartItemList(new ArrayList<>()).build();
        cart.getCartItemList().add(CartItem.builder().cart(cart).goods(goods).quantity(1).price(1000).build());
        CreateCartReq request = CreateCartReq.builder().goodsIdx(11L).popupIdx(7L).build();
        when(goodsRepository.findByGoodsIdxAndPopupIdx(11L, 7L)).thenReturn(Optional.of(goods));
        when(cartRepository.findByCustomerIdxAndPopupIdx(3L, 7L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.createCart(user, request))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        org.assertj.core.api.Assertions.assertThat(exception.getErrorCode())
                                .isEqualTo(ServiceErrorCode.CART_REGISTER_FAIL_ITEM_EXIST));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void createsCartAndItemWhenThePopupCartDoesNotExist() {
        CreateCartReq request = CreateCartReq.builder().goodsIdx(11L).popupIdx(7L).build();
        when(goodsRepository.findByGoodsIdxAndPopupIdx(11L, 7L)).thenReturn(Optional.of(goods));
        when(cartRepository.findByCustomerIdxAndPopupIdx(3L, 7L)).thenReturn(Optional.empty());

        cartService.createCart(user, request);

        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
    }
}
