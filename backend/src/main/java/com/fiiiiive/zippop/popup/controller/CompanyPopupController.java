package com.fiiiiive.zippop.popup.controller;

import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.GetOrdersRes;
import com.fiiiiive.zippop.payout.model.dto.GetPopupPayoutsRes;
import com.fiiiiive.zippop.popup.model.dto.GetPopupRes;
import com.fiiiiive.zippop.popup.service.CompanyPopupService;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "company-api", description = "Company Search Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company/popups")
public class CompanyPopupController {

    private final CompanyPopupService companyPopupService;

    // 내 팝업 목록 조회 (기업용)
    @GetMapping
    public ResponseEntity<BaseResponse<Page<GetPopupRes>>> getCompanyPopups(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetPopupRes> res = companyPopupService.getCompanyPopups(user, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업의 정산 목록 조회 (기업용)
    @GetMapping("/{popupIdx}/payouts")
    public ResponseEntity<BaseResponse<Page<GetPopupPayoutsRes>>> getCompanyPopupPayouts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetPopupPayoutsRes> res = companyPopupService.getCompanyPopupPayouts(user, popupIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.PAYOUT_SEARCH_SUCCESS, res));
    }

    // 팝업 예약 목록 조회 (기업용)
    @GetMapping("/{popupIdx}/reserves")
    public ResponseEntity<BaseResponse<Page<GetReserveRes>>> getCompanyPopupReserves(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetReserveRes> response = companyPopupService.getCompanyPopupReserves(user, popupIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    // 팝업의 주문 목록 조회 (기업용)
    @GetMapping("/{popupIdx}/orders")
    public ResponseEntity<BaseResponse<Page<GetOrdersRes>>> getCompanyPopupOrdersList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetOrdersRes> res = companyPopupService.getCompanyPopupOrdersList(user, popupIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.ORDERS_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업의 주문 상세 조회 (기업용)
    @GetMapping("/{popupIdx}/orders/{ordersIdx}")
    public ResponseEntity<BaseResponse<GetOrdersRes>> getPopupOrdersDetail(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @PathVariable Long ordersIdx
    ) {
        GetOrdersRes res = companyPopupService.getPopupOrdersDetail(user, popupIdx, ordersIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.ORDERS_SEARCH_SUCCESS, res));
    }

}
