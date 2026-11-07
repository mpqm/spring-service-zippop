package com.fiiiiive.zippop.reserve.controller;


import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.reserve.model.dto.*;
import com.fiiiiive.zippop.reserve.service.ReserveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "reserve-api", description = "Reserve Management")
@Slf4j
@RestController
@RequestMapping("/api/v1/reserves")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    // 예약 생성
    @PostMapping
    public ResponseEntity<SuccessResponse<CreateReserveRes>> createReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateReserveReq req
    ) {
        CreateReserveRes res = reserveService.createReserve(user, req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.RESERVE_REGISTER_SUCCESS, res));
    }

    // 예약 목록 조회 (기업용)
    @GetMapping("/{popupIdx}")
    public ResponseEntity<SuccessResponse<Page<GetReserveRes>>> getCompanyPopupReserves(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetReserveRes> response = reserveService.getReserves(user, popupIdx, page, size);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.RESERVE_SEARCH_ALL_SUCCESS, response));
    }

    // 예약 삭제
    @DeleteMapping("/{reserveIdx}")
    public ResponseEntity<SuccessResponse<String>> deleteReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reserveIdx
    ) {
        reserveService.deleteReserve(user, reserveIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.RESERVE_DELETE_SUCCESS));
    }

    // 예약 신청
    @GetMapping("/{reserveIdx}/enrollment")
    public ResponseEntity<SuccessResponse<EnrollReserveRes>> enrollReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse res,
            @PathVariable Long reserveIdx
    ) {
        EnrollReserveRes response = reserveService.enrollReserve(res, user, reserveIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.RESERVE_ENROLL_SUCCESS, response));
    }

    // 예약 취소
    @DeleteMapping("/{reserveIdx}/enrollment")
    public ResponseEntity<SuccessResponse<String>> cancelReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest req,
            HttpServletResponse res,
            @PathVariable Long reserveIdx
    ) throws ServiceException {
        String response = reserveService.cancelReserve(req, res, user, reserveIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.RESERVE_CANCEL_SUCCESS, response));
    }

    // (WebSocket) 예약 상태 업데이트
    @MessageMapping("/reserve/status")
    public void updateReserveStatus(
        Principal principal,
        @Payload GetReserveQueueReq getReserveQueueReq) throws ServiceException {
        reserveService.status(principal, getReserveQueueReq);
    }



}
