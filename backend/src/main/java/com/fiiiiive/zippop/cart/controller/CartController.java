package com.fiiiiive.zippop.cart.controller;

import com.fiiiiive.zippop.cart.service.CartService;
import com.fiiiiive.zippop.cart.model.dto.CreateCartReq;
import com.fiiiiive.zippop.cart.model.dto.CountCartRes;
import com.fiiiiive.zippop.cart.model.dto.CreateCartRes;
import com.fiiiiive.zippop.cart.model.dto.GetCartRes;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody CreateCartReq dto) throws BaseException {
        CreateCartRes response = cartService.register(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_REGISTER_SUCCESS, response));
    }

    @GetMapping("/count")
    public ResponseEntity<BaseResponse> count(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long cartIdx,
        @RequestParam Long operation) throws BaseException {
        CountCartRes response  = cartService.count(customUserDetails, cartIdx, operation);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_COUNT_SUCCESS, response));
    }

    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse> searchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException {
        List<GetCartRes> response = cartService.searchAll(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_SEARCH_LIST_SUCESS, response));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long cartIdx) throws BaseException {
        cartService.delete(customUserDetails, cartIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_DELETE_SUCCESS));
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<BaseResponse> deleteAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException {
        cartService.deleteAll(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.CART_DELETE_ALL_SUCCESS));
    }
}