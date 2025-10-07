package com.fiiiiive.zippop.payout.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.payout.dto.PayoutDto;
import com.fiiiiive.zippop.payout.service.PayoutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "payout-api", description = "Payout")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/payout")
public class PayoutController {

    private final PayoutService payoutService;

    // 정산 목록 조회
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<PayoutDto.SearchPayoutRes>>> search (
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<PayoutDto.SearchPayoutRes> response = payoutService.searchPayout(customUserDetails,storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.PAYOUT_SEARCH_SUCCESS, response));

    }
}
