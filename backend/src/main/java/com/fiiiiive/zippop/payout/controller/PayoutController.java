package com.fiiiiive.zippop.payout.controller;

import com.fiiiiive.zippop.global.api.SuccessCode;
import com.fiiiiive.zippop.global.api.SuccessResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.payout.model.dto.GetPayoutsRes;
import com.fiiiiive.zippop.payout.service.PayoutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "company-api", description = "Company Search Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payouts")
public class PayoutController {

    private final PayoutService payoutService;

    // 팝업의 정산 목록 조회 (기업용)
    @GetMapping("/{popupIdx}")
    public ResponseEntity<SuccessResponse<Page<GetPayoutsRes>>> getCompanyPopupPayouts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetPayoutsRes> res = payoutService.getPayouts(user, popupIdx, page, size);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.PAYOUT_SEARCH_SUCCESS, res));
    }

}
