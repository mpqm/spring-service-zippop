package com.fiiiiive.zippop.orders.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.application.OrdersFacade;
import com.fiiiiive.zippop.orders.model.OrdersDto;
import com.fiiiiive.zippop.orders.service.OrdersService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "orders-api", description = "Order Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final OrdersFacade ordersFacade;
    
    // 주문 생성
    @PostMapping
    public ResponseEntity<BaseResponse<OrdersDto.CreateOrdersRes>> createOrders(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody OrdersDto.CreateOrdersReq req
    ) throws BaseException, IamportResponseException, IOException {
        OrdersDto.CreateOrdersRes res = ordersFacade.createOrders(user, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(BaseMessage.ORDERS_PAY_SUCCESS, res));
    }

    // 주문 상태 변경
    @PatchMapping("/{orderIdx}")
    public ResponseEntity<BaseResponse<OrdersDto.UpdateOrdersRes>> updateOrders(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderIdx,
            @RequestBody OrdersDto.UpdateOrdersReq req
    ) throws BaseException, IamportResponseException, IOException{
        OrdersDto.UpdateOrdersRes res = ordersFacade.updateOrders(user, orderIdx, req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.ORDERS_COMPLETE_SUCCESS, res));
    }

    // 고객의 주문 상세 조회
    @GetMapping("/{ordersIdx}")
    public ResponseEntity<BaseResponse<OrdersDto.GetOrdersRes>> getOrder(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long ordersIdx) throws BaseException {

        OrdersDto.GetOrdersRes res = ordersService.getOrder(user, ordersIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.ORDERS_SEARCH_SUCCESS, res));
    }

    // 고객의 주문 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<Page<OrdersDto.GetOrdersRes>>> getOrders(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) throws BaseException {

        Page<OrdersDto.GetOrdersRes> res = ordersService.getOrders(user, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.ORDERS_SEARCH_ALL_SUCCESS, res));
    }

}