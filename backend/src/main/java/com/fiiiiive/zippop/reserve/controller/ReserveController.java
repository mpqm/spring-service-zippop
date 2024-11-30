package com.fiiiiive.zippop.reserve.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveReq;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveRes;
import com.fiiiiive.zippop.reserve.service.ReserveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "reserve-api", description = "Reserve")
@Slf4j
@RestController
@RequestMapping("/api/v1/reserve")
@RequiredArgsConstructor
public class ReserveController {
    private final ReserveService reserveService;

    // 예약 생성
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody CreateReserveReq dto) throws BaseException {
        CreateReserveRes response = reserveService.register(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESERVE_REGISTER_SUCCESS,response));
    }

    // 예약 신청: email -> @AuthenticationPrincipal CustomUserDetails customUserDetails // 테스트 중
    @GetMapping("/enroll")
    public ResponseEntity<BaseResponse> enroll(
        HttpServletResponse res,
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long reserveIdx) throws BaseException {
        String response = reserveService.enroll(res, customUserDetails, reserveIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESERVE_ENROLL_SUCCESS, response));
    }

    // 예약 취소: email -> @AuthenticationPrincipal CustomUserDetails customUserDetails // 테스트 중
    @GetMapping("/cancel")
    public ResponseEntity<BaseResponse> cancel(String userId, @RequestParam Long reserveIdx) throws BaseException {
        String response = reserveService.cancel(userId, reserveIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    @GetMapping("/status")
    public ResponseEntity<BaseResponse> reserveStatus(String reserveUUID, String reserveWaitingUUID){
        String response = reserveService.reserveStatus(reserveUUID,reserveWaitingUUID);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_RESERVE_SEARCH_STATUS_SUCCESS, response));
    }

}
