package com.fiiiiive.zippop.cart.controller;

import com.fiiiiive.zippop.cart.model.dto.*;
import com.fiiiiive.zippop.cart.service.CartItemService;
import com.fiiiiive.zippop.cart.service.CartService;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final CartItemService cartItemService;

    // 장바구니 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody CreateCartReq dto) throws BaseException {

        cartService.register(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_REGISTER_SUCCESS));
    }

    // 장바구니 목록 조회
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<SearchCartRes>>> searchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<SearchCartRes> response = cartService.searchAll(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_SEARCH_ALL_SUCCESS, response));
    }

    // 장바구니 아이템 수량 조절
    @GetMapping("/item/count")
    public ResponseEntity<BaseResponse<Void>> itemCount(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long cartItemIdx,
        @RequestParam Boolean flag) throws BaseException {

        cartItemService.itemCount(customUserDetails, cartItemIdx, flag);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_ITEM_COUNT_SUCCESS));
    }

    // 장바구니 아이템 전체 조회(고객)
    @GetMapping("/item/search-all")
    public ResponseEntity<BaseResponse> itemSearchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        List<SearchCartItemRes> response = cartItemService.itemSearchAll(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_ITEM_SEARCH_ALL_SUCCESS, response));
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/item/delete")
    public ResponseEntity<BaseResponse> itemDelete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long cartItemIdx) throws BaseException {

        cartItemService.itemDelete(cartItemIdx, customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_ITEM_DELETE_SUCCESS));
    }

    // 장바구니 아이템 전체 삭제
    @DeleteMapping("/item/delete-all")
    public ResponseEntity<BaseResponse> itemDeleteAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        cartItemService.itemDeleteAll(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_ITEM_DELETE_ALL_SUCCESS));
    }
}