package com.fiiiiive.zippop.domain.reserve.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.domain.reserve.dto.ReserveDto;
import com.fiiiiive.zippop.domain.reserve.service.ReserveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "reserve-api", description = "Reserve")
@Slf4j
@RestController
@RequestMapping("/api/v1/reserve")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    // 예약 생성
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<ReserveDto.CreateReserveRes>> registerReserve(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @Valid @RequestBody ReserveDto.CreateReserveReq dto) throws BaseException {

        ReserveDto.CreateReserveRes response = reserveService.registerReserve(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_REGISTER_SUCCESS,response));
    }

    // 예약 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<String>> deleteReserve(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestParam Long reserveIdx) throws BaseException {
        reserveService.deleteReserve(customUserDetails, storeIdx, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_DELETE_SUCCESS));
    }

    // 예약 신청
    @GetMapping("/enroll")
    public ResponseEntity<BaseResponse<ReserveDto.EnrollReserveRes>> enrollReserve(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        HttpServletResponse res,
        @RequestParam Long reserveIdx) throws BaseException {

        ReserveDto.EnrollReserveRes response = reserveService.enrollReserve(res, customUserDetails, reserveIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_ENROLL_SUCCESS, response));
    }

    // 예약 취소 (사용자)
    @GetMapping("/cancel")
    public ResponseEntity<BaseResponse<String>> cancelReserve(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        HttpServletRequest req,
        HttpServletResponse res,
        @RequestParam Long reserveIdx,
        @RequestParam Long storeIdx) throws BaseException {

        String response = reserveService.cancelReserve(req, res, customUserDetails, reserveIdx, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    // 예약 인가
    @GetMapping("/access")
    public ResponseEntity<BaseResponse<Void>> access() throws BaseException {
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_ACCESS_SUCCESS));
    }

    // 예약 목록 조회
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<ReserveDto.SearchReserveRes>>> searchAllReserve (
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Long storeIdx,
        @RequestParam int page,
        @RequestParam int size ) throws BaseException {

        Page<ReserveDto.SearchReserveRes> response = reserveService.searchAllReserve(storeIdx, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    // 예약 목록 조회(기업용)
    @GetMapping("/search-all/as-company")
    public ResponseEntity<BaseResponse<Page<ReserveDto.SearchReserveRes>>> searchAllReserveAsCompany(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size ) throws BaseException {

        Page<ReserveDto.SearchReserveRes> response = reserveService.searchAllReserveAsCompany(customUserDetails,storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.RESERVE_CANCEL_SUCCESS, response));
    }

    // 예약 상태(소켓통신)
    @MessageMapping("/reserve/status")
    public void status(
        @AuthenticationPrincipal Principal principal,
        @Payload ReserveDto.StatusReserveReq statusReserveReq) throws BaseException {

        reserveService.status(principal, statusReserveReq);
    }

//    // 폴링방식
//    @GetMapping("/status")
//    public ResponseEntity<BaseResponse> status(
//        @AuthenticationPrincipal CustomUserDetails customUserDetails,
//        @RequestParam Long reserveIdx) throws BaseException {
//
//        String response = reserveService.status(customUserDetails, reserveIdx);
//        return ResponseEntity.ok(new BaseResponse(BaseMessage.RESERVE_SEARCH_STATUS_SUCCESS, response));
//    }

}
