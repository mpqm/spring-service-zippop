package com.fiiiiive.zippop.orders.controller;


import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.application.OrdersFacade;
import com.fiiiiive.zippop.orders.model.dto.*;
import com.fiiiiive.zippop.orders.service.OrdersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "orders-api", description = "Order Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final OrdersFacade ordersFacade;

    @PostMapping("/merchant-id")
    public ResponseEntity<SuccessResponse<CreatePaymentIntentRes>> issueMerchantUid(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreatePaymentIntentReq req) {
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_PAY_SUCCESS,
                ordersService.issueMerchantUid(user, req)));
    }
    
    // 주문 생성
    @PostMapping
    public ResponseEntity<SuccessResponse<CreateOrdersRes>> createOrders(
        @AuthenticationPrincipal CustomUserDetails user,
        @Valid @RequestBody CreateOrdersReq req
    ) {
        CreateOrdersRes res = ordersFacade.createOrders(user, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse<>(SuccessCode.ORDERS_PAY_SUCCESS, res));
    }

    // PG webhook: 응답 유실 시에도 서버 결제 의도를 이용해 주문을 복구하거나 환불 보상을 등록한다.
    @PostMapping("/payments/webhook")
    public ResponseEntity<SuccessResponse<CreateOrdersRes>> paymentWebhook(@Valid @RequestBody PaymentWebhookReq req) {
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_PAY_SUCCESS,
                ordersService.reconcilePayment(req)));
    }

    // 주문 상태 변경
    @PatchMapping("/{orderIdx}")
    public ResponseEntity<SuccessResponse<UpdateOrdersRes>> updateOrders(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderIdx,
            @Valid @RequestBody UpdateOrdersReq req
    ) {
        UpdateOrdersRes res = ordersFacade.updateOrders(user, orderIdx, req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_COMPLETE_SUCCESS, res));
    }

    // 고객의 주문 상세 조회
    @GetMapping("/{ordersIdx}")
    public ResponseEntity<SuccessResponse<GetOrdersRes>> getOrder(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long ordersIdx) {

        GetOrdersRes res = ordersService.getOrder(user, ordersIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_SEARCH_SUCCESS, res));
    }

    // 고객의 주문 목록 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<GetOrdersRes>>> getOrders(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        Page<GetOrdersRes> res = ordersService.getOrders(user, page, size);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업의 주문 목록 조회 (기업용)
    @GetMapping("/co/{popupIdx}")
    public ResponseEntity<SuccessResponse<Page<GetOrdersRes>>> getCompanyPopupOrders(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetOrdersRes> res = ordersService.getCompanyPopupOrders(user, popupIdx, page, size);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업의 주문 상세 조회 (기업용)
    @GetMapping("/{ordersIdx}/co/{popupIdx}")
    public ResponseEntity<SuccessResponse<GetOrdersRes>> getCompanyPopupOrder(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @PathVariable Long ordersIdx
    ) {
        GetOrdersRes res = ordersService.getCompanyPopupOrder(user, popupIdx, ordersIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.ORDERS_SEARCH_SUCCESS, res));
    }


}
