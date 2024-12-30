package com.fiiiiive.zippop.orders.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.OrdersDto;
import com.fiiiiive.zippop.orders.service.OrdersService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "orders-api", description = "Orders")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;

    // 주문 검증(재고용)
    @GetMapping("/verify/reserve")
    public ResponseEntity<BaseResponse<OrdersDto.VerifyOrdersRes>> verifyOrdersReserve(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestParam String impUid) throws BaseException, IamportResponseException, IOException{

        OrdersDto.VerifyOrdersRes response = ordersService.verifyOrdersReserve(customUserDetails, impUid, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_PAY_SUCCESS,response));
    }

    // 주문 검증(예약용)
    @GetMapping("/verify/stock")
    public ResponseEntity<BaseResponse<OrdersDto.VerifyOrdersRes>> verifyOrdersStock(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestParam String impUid) throws BaseException, IamportResponseException, IOException{

        OrdersDto.VerifyOrdersRes response = ordersService.verifyOrdersStock(customUserDetails, impUid, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_PAY_SUCCESS,response));
    }


    // 주문 취소
    @GetMapping("/cancel")
    public ResponseEntity<BaseResponse<IamportResponse<Payment>>> cancelOrders(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long ordersIdx) throws BaseException, IamportResponseException, IOException{

        ordersService.cancelOrders(customUserDetails, ordersIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_CANCEL_SUCCESS));
    }

    // 주문 확정
    @GetMapping("/complete")
    public ResponseEntity<BaseResponse<OrdersDto.VerifyOrdersRes>> completeOrders(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam(required = false) Long storeIdx,
        @RequestParam Long ordersIdx) throws BaseException {

        ordersService.completeOrders(customUserDetails, storeIdx, ordersIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_COMPLETE_SUCCESS));
    }

    // 고객 주문 조회
    @GetMapping("/search/as-customer")
    public ResponseEntity<BaseResponse<OrdersDto.SearchOrdersRes>> searchOrdersAsCustomer (
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long ordersIdx )throws BaseException {

        OrdersDto.SearchOrdersRes response = ordersService.searchOrdersAsCustomer(customUserDetails, ordersIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_SEARCH_SUCCESS,response));
    }

    // 고객 주문 목록 조회
    @GetMapping("/search-all/as-customer")
    public ResponseEntity<BaseResponse<Page<OrdersDto.SearchOrdersRes>>> searchAllOrdersAsCustomer(
        @RequestParam int page,
        @RequestParam int size,
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {

        Page<OrdersDto.SearchOrdersRes> response = ordersService.searchAllOrdersAsCustomer(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_SEARCH_ALL_SUCCESS,response));
    }

    // 기업 고객 주문 조회
    @GetMapping("/search/as-company")
    public ResponseEntity<BaseResponse<OrdersDto.SearchOrdersRes>> searchOrdersAsCompany (
        @RequestParam Long ordersIdx,
        @RequestParam Long storeIdx,
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {

        OrdersDto.SearchOrdersRes response = ordersService.searchOrdersAsCompany(customUserDetails, storeIdx, ordersIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_SEARCH_SUCCESS, response));
    }

    // 기업 고객 주문 목록 조회
    @GetMapping("/search-all/as-company")
    public ResponseEntity<BaseResponse<Page<OrdersDto.SearchOrdersRes>>> searchAllOrdersAsCompany(
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size,
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {

        Page<OrdersDto.SearchOrdersRes> response = ordersService.searchAllOrdersAsCompany(customUserDetails, storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.ORDERS_SEARCH_ALL_SUCCESS,response));
    }

}