package com.fiiiiive.zippop.domain.cart.controller;

import com.fiiiiive.zippop.domain.cart.dto.CartDto;
import com.fiiiiive.zippop.domain.cart.service.CartService;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "cart-api", description = "Cart")
@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> registerCart(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @Valid @RequestBody CartDto.CreateCartReq dto) throws BaseException {

        cartService.registerCart(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.CART_REGISTER_SUCCESS));
    }

    // 장바구니 목록 조회
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<CartDto.SearchCartRes>>> searchAllCart(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Integer page,
        @RequestParam Integer size) throws BaseException {

        Page<CartDto.SearchCartRes> response = cartService.searchAllCart(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.CART_SEARCH_ALL_SUCCESS, response));
    }

    // 장바구니 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<Void>> deleteAllCartItem(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        cartService.deleteAllCartItem(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.CART_ITEM_DELETE_ALL_SUCCESS));
    }

    // 장바구니 아이템 수량 조절
    @GetMapping("/item/count")
    public ResponseEntity<BaseResponse<Void>> countCartItem(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long cartItemIdx,
        @RequestParam Boolean operation) throws BaseException {

        cartService.countCartItem(customUserDetails, cartItemIdx, operation);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.CART_ITEM_COUNT_SUCCESS));
    }

    // 장바구니 아이템 목록 조회
    @GetMapping("/item/search-all")
    public ResponseEntity<BaseResponse<List<CartDto.SearchCartItemRes>>> searchAllCartItem(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        List<CartDto.SearchCartItemRes> response = cartService.searchAllCartItem(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.CART_ITEM_SEARCH_ALL_SUCCESS, response));
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/item/delete")
    public ResponseEntity<BaseResponse<Void>> deleteCartItem(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long cartItemIdx) {

        cartService.deleteCartItem(customUserDetails, cartItemIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.CART_ITEM_DELETE_SUCCESS));
    }

}