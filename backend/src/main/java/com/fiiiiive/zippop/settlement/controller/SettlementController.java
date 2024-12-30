package com.fiiiiive.zippop.settlement.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.settlement.model.dto.SettlementDto;
import com.fiiiiive.zippop.settlement.model.entity.Settlement;
import com.fiiiiive.zippop.settlement.repository.SettlementRepository;
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
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.SETTLEMENT_SEARCH_SUCCESS, response));

    }
}
