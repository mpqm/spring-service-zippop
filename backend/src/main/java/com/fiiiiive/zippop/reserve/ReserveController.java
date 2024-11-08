package com.fiiiiive.zippop.reserve;


import com.fiiiiive.zippop.common.exception.BaseException;
import com.fiiiiive.zippop.common.responses.BaseResponse;
import com.fiiiiive.zippop.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.member.model.CustomUserDetails;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveReq;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "popup-reserve-api", description = "Reserve")
@Slf4j
@RestController
@RequestMapping("/api/v1/popup-reserve")
@RequiredArgsConstructor
public class ReserveController {
    private final ReserveService reserveService;

    // 예약 생성
    @PostMapping("/create")
    public ResponseEntity<BaseResponse> create(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CreateReserveReq dto) throws BaseException {
        CreateReserveRes response = reserveService.create(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_RESERVE_CREATE_SUCCESS,response));
    }

    // 예약 신청: email -> @AuthenticationPrincipal CustomUserDetails customUserDetails // 테스트 중
    @GetMapping("/enroll")
    public ResponseEntity<BaseResponse> enroll(HttpServletRequest req, HttpServletResponse res,
                                                String email, @RequestParam Long reserveIdx) throws IOException {
        String response = reserveService.enroll(req, res, email, reserveIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_RESERVE_ENROLL_SUCCESS, response));
    }

    // 예약 취소: email -> @AuthenticationPrincipal CustomUserDetails customUserDetails // 테스트 중
    @GetMapping("/cancel")
    public ResponseEntity<BaseResponse> cancel(String email, @RequestParam Long reserveIdx) throws BaseException {
        String response = reserveService.cancel(email, reserveIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_RESERVE_CANCEL_SUCCESS, response));
    }

    @GetMapping("/reserve-status")
    public ResponseEntity<BaseResponse> reserveStatus(String reserveUUID, String reserveWaitingUUID){
        String response = reserveService.reserveStatus(reserveUUID,reserveWaitingUUID);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_RESERVE_SEARCH_STATUS_SUCCESS, response));
    }

}
