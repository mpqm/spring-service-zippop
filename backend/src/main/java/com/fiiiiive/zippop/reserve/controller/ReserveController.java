package com.fiiiiive.zippop.reserve.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.reserve.model.ReserveDto;
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

@Tag(name = "reserve-api", description = "Reservation Management")
@Slf4j
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    // 예약 생성
    @PostMapping
    public ResponseEntity<BaseResponse<ReserveDto.CreateReserveRes>> createReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ReserveDto.CreateReserveReq req
    ) throws BaseException {
        ReserveDto.CreateReserveRes res = reserveService.createReservation(user, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(BaseMessage.RESERVE_REGISTER_SUCCESS, res));
    }

    // 예약 삭제
    @DeleteMapping("/{reserveIdx}")
    public ResponseEntity<BaseResponse<String>> deleteReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reserveIdx
    ) throws BaseException {
        reserveService.deleteReservation(user, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_DELETE_SUCCESS));
    }

    // 예약 신청
    @PostMapping("/{reserveIdx}/enrollment")
    public ResponseEntity<BaseResponse<ReserveDto.EnrollReserveRes>> enrollReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse res,
            @PathVariable Long reserveIdx
    ) throws BaseException {
        ReserveDto.EnrollReserveRes response = reserveService.enrollReservation(res, user, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_ENROLL_SUCCESS, response));
    }

    // 예약 취소
    @DeleteMapping("/{reserveIdx}/enrollment")
    public ResponseEntity<BaseResponse<String>> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest req,
            HttpServletResponse res,
            @PathVariable Long reserveIdx
    ) throws BaseException {
        String response = reserveService.cancelReservation(req, res, user, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    // 예약 상태 업데이트 (WebSocket)
    @MessageMapping("/status")
    public void updateReservationStatus(
        @AuthenticationPrincipal Principal principal,
        @Payload ReserveDto.StatusReserveReq statusReserveReq) throws BaseException {
        reserveService.status(principal, statusReserveReq);
    }

}
