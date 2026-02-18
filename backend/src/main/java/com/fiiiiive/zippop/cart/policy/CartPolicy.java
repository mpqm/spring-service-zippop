package com.fiiiiive.zippop.cart.policy;

import com.fiiiiive.zippop.cart.model.Cart;
import com.fiiiiive.zippop.cart.model.CartItem;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CartPolicy {

    // 장바구니 소유자 확인
    public void validateCartOwner(Cart cart, CustomUserDetails user) {
        if (!Objects.equals(cart.getCustomer().getIdx(), user.getIdx())){
            throw new BaseException(BaseMessage.CART_DELETE_ALL_FAIL_UNAUTHORIZED);
        }
    }

    // 장바구니아이템 소유자 확인
    public void validateCartItemOwner(CartItem cartItem, CustomUserDetails user) {
        if (!Objects.equals(cartItem.getCart().getCustomer().getIdx(), user.getIdx())){
            throw new BaseException(BaseMessage.CART_DELETE_ALL_FAIL_UNAUTHORIZED);
        }
    }

}
