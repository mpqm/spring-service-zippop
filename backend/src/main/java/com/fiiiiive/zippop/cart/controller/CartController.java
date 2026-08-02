package com.fiiiiive.zippop.cart.controller;

import com.fiiiiive.zippop.cart.model.dto.CreateCartReq;
import com.fiiiiive.zippop.cart.model.dto.GetCartItemRes;
import com.fiiiiive.zippop.cart.model.dto.GetCartRes;
import com.fiiiiive.zippop.cart.service.CartService;
import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "cart-api", description = "Shopping Cart Management")
@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 생성
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createCart(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateCartReq req
    ) {
        cartService.createCart(user, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse<>(SuccessCode.CART_REGISTER_SUCCESS));
    }

    // 현재 사용자의 장바구니 목록 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<GetCartRes>>> getCarts(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<GetCartRes> res = cartService.getCarts(user, page, size);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.CART_SEARCH_ALL_SUCCESS, res));
    }

    // 특정 장바구니 삭제
    @DeleteMapping("/{cartIdx}")
    public ResponseEntity<SuccessResponse<Void>> deleteCart(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartIdx
    ) {
        cartService.deleteCart(user, cartIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.CART_ITEM_DELETE_ALL_SUCCESS));
    }

    // 장바구니의 아이템 목록 조회
    @GetMapping("/{cartIdx}/items")
    public ResponseEntity<SuccessResponse<List<GetCartItemRes>>> getCartItems(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartIdx
    ) {
        List<GetCartItemRes> res = cartService.getCartItems(user, cartIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.CART_ITEM_SEARCH_ALL_SUCCESS, res));
    }

    // 장바구니 아이템 수량 변경
    @PatchMapping("/{cartIdx}/items/{cartItemIdx}/quantity")
    public ResponseEntity<SuccessResponse<Void>> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartIdx,
            @PathVariable Long cartItemIdx,
            @RequestParam String operation
    ) {
        cartService.updateCartItemQuantity(user, cartIdx, cartItemIdx, operation);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.CART_ITEM_COUNT_SUCCESS));
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/{cartIdx}/items/{cartItemIdx}")
    public ResponseEntity<SuccessResponse<Void>> deleteCartItem(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartIdx,
            @PathVariable Long cartItemIdx
    ) {
        cartService.deleteCartItem(user, cartIdx, cartItemIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.CART_ITEM_DELETE_SUCCESS));
    }

}
