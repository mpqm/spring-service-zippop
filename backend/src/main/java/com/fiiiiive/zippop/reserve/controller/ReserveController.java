package com.fiiiiive.zippop.reserve.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveReq;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveRes;
import com.fiiiiive.zippop.reserve.model.dto.EnrollReserveRes;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveQueueReq;
import com.fiiiiive.zippop.reserve.service.ReserveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<BaseResponse<CreateReserveRes>> createReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateReserveReq req
    ) {
        CreateReserveRes res = reserveService.createReserve(user, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(BaseMessage.RESERVE_REGISTER_SUCCESS, res));
    }

    // 예약 삭제
    @DeleteMapping("/{reserveIdx}")
    public ResponseEntity<BaseResponse<String>> deleteReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reserveIdx
    ) {
        reserveService.deleteReserve(user, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_DELETE_SUCCESS));
    }

    // 예약 신청
    @GetMapping("/{reserveIdx}/enrollment")
    public ResponseEntity<BaseResponse<EnrollReserveRes>> enrollReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse res,
            @PathVariable Long reserveIdx
    ) {
        EnrollReserveRes response = reserveService.enrollReserve(res, user, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_ENROLL_SUCCESS, response));
    }

    // 예약 취소
    @DeleteMapping("/{reserveIdx}/enrollment")
    public ResponseEntity<BaseResponse<String>> cancelReserve(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest req,
            HttpServletResponse res,
            @PathVariable Long reserveIdx
    ) throws BaseException {
        String response = reserveService.cancelReserve(req, res, user, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    // (WebSocket) 예약 상태 업데이트
    @MessageMapping("/reserve/status")
    public void updateReserveStatus(
        @AuthenticationPrincipal Principal principal,
        @Payload GetReserveQueueReq getReserveQueueReq) throws BaseException {
        reserveService.status(principal, getReserveQueueReq);
    }

}
