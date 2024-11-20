package com.fiiiiive.zippop.orders.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.SearchOrdersDetailRes;
import com.fiiiiive.zippop.orders.model.dto.SearchOrdersRes;
import com.fiiiiive.zippop.orders.service.OrdersService;
import com.fiiiiive.zippop.orders.model.dto.VerifyOrdersRes;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import retrofit2.HttpException;

import java.io.IOException;
import java.util.List;

@Tag(name = "orders-api", description = "Orders")
@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*",allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders")
@Slf4j
public class OrdersController {
    private final OrdersService ordersService;

    // 결제 검증
    @GetMapping("/verify")
    public ResponseEntity<BaseResponse<VerifyOrdersRes>> verify(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam String impUid,
        @RequestParam Boolean flag) throws BaseException, IamportResponseException, IOException{
        VerifyOrdersRes response = ordersService.verifyOrders(customUserDetails, impUid, flag);

        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_PAY_SUCCESS,response));
    }

    // 결제 취소
    @GetMapping("/cancel")
    public ResponseEntity<BaseResponse<VerifyOrdersRes>> cancel(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long orderIdx) throws BaseException, IamportResponseException, IOException{
        ordersService.cancelOrders(customUserDetails, orderIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_PAY_REFUND_FAIL_IS_COMPLETE));
    }
    
    // 주문 확정
    @GetMapping("/complete")
    public ResponseEntity<BaseResponse<VerifyOrdersRes>> complete(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) Long storeIdx,
            @RequestParam Long orderIdx) throws BaseException, IamportResponseException, IOException{
        ordersService.completeOrders(customUserDetails, storeIdx, orderIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_PAY_REFUND_FAIL_IS_COMPLETE));
    }

    // 고객 주문 상세 조회
    @GetMapping("/search/as-customer")
    public ResponseEntity<BaseResponse<List<SearchOrdersDetailRes>>> searchOrdersAsCustomer (
        @RequestParam Long ordersIdx,
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {
        SearchOrdersRes response = ordersService.searchOrdersAsCustomer(customUserDetails, ordersIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_ORDERS_SEARCH_SUCCESS,response));
    }

    // 고객 주문 목록 조회
    @GetMapping("/search-all/as-customer")
    public ResponseEntity<BaseResponse<Page<SearchOrdersRes>>> searchAllOrders(
        @RequestParam int page,
        @RequestParam int size,
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {
        Page<SearchOrdersRes> response = ordersService.searchAllOrdersAsCustomer(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_ORDERS_SEARCH_SUCCESS,response));
    }

    // 기업 고객 주문 상세 조회
    @GetMapping("/search/as-company")
    public ResponseEntity<BaseResponse<List<SearchOrdersDetailRes>>> searchOrdersAsCompany (
        @RequestParam Long ordersIdx,
        @RequestParam Long storeIdx,
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {
        SearchOrdersRes response = ordersService.searchOrdersAsCompany(customUserDetails, storeIdx, ordersIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_ORDERS_SEARCH_SUCCESS, response));
    }

    // 기업 고객 주문 목록 조회
    @GetMapping("/search-all/as-company")
    public ResponseEntity<BaseResponse<Page<SearchOrdersRes>>> searchAllOrdersAsCompany(
            @RequestParam Long storeIdx,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {
        Page<SearchOrdersRes> response = ordersService.searchAllOrdersAsCompany(customUserDetails, storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_ORDERS_SEARCH_SUCCESS,response));
    }


}