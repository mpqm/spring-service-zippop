package com.fiiiiive.zippop.settlement.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.settlement.dto.SettlementDto;
import com.fiiiiive.zippop.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "orders-api", description = "Orders")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    // 정산 목록 조회
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<SettlementDto.SearchSettlementRes>>> search (
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<SettlementDto.SearchSettlementRes> response = settlementService.searchSettlement(customUserDetails,storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.SETTLEMENT_SEARCH_SUCCESS, response));

    }
}
