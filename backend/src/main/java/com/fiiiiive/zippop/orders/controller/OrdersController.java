package com.fiiiiive.zippop.orders.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.SearchCustomerOrdersRes;
import com.fiiiiive.zippop.orders.service.OrdersService;
import com.fiiiiive.zippop.orders.model.dto.SearchCompanyOrdersRes;
import com.fiiiiive.zippop.orders.model.dto.VerifyOrdersRes;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "orders-api", description = "Orders")
@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*",allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders")
public class OrdersController {
    private final OrdersService ordersService;

    // 결제 검증
    @GetMapping("/verify")
    public BaseResponse<VerifyOrdersRes> verify(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam String impUid,
        @RequestParam int operation) throws BaseException, IamportResponseException, IOException{
        VerifyOrdersRes response = ordersService.verify(customUserDetails, impUid, operation);
        return new BaseResponse(BaseResponseMessage.POPUP_PAY_SUCCESS,response);
    }

    // 고객 주문 조회
    @GetMapping("/search-customer")
    public BaseResponse<List<VerifyOrdersRes>> searchCustomer(
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {
        List<SearchCustomerOrdersRes> response = ordersService.searchCustomer(customUserDetails);
        return new BaseResponse(BaseResponseMessage.POPUP_ORDERS_SEARCH_SUCCESS,response);
    }

    // 기업 주문 조회
    @GetMapping("/search-company")
    public BaseResponse<List<VerifyOrdersRes>> searchCompany(
        @AuthenticationPrincipal CustomUserDetails customUserDetails )throws BaseException {
        List<SearchCompanyOrdersRes> response = ordersService.searchCompany(customUserDetails);
        return new BaseResponse(BaseResponseMessage.POPUP_ORDERS_SEARCH_SUCCESS,response);
    }

}