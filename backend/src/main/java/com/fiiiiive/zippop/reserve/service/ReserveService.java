package com.fiiiiive.zippop.reserve.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveReq;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveRes;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.global.utils.JwtUtil;
import com.fiiiiive.zippop.global.utils.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {
    private final ReserveRepository reserveRepository;
    private final StoreRepository storeRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public CreateReserveRes register(CustomUserDetails customUserDetails, CreateReserveReq dto) throws BaseException {
        // 1. 예외: 기업 회원이 아닐 때, 스토어 조회 안될 때, 스토어의 이메일과 인증된 사용자의 이메일이 다를 때
        Store store = storeRepository.findById(dto.getStoreIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
        );
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.RESERVE_REGISTER_FAIL_INVALID_MEMBER);
        }

        // 2. Reserve 저장
        String workingUUID = UUID.randomUUID().toString();
        String waitingUUID = UUID.randomUUID().toString();
        Reserve reserve = Reserve.builder()
                .reservePeople(dto.getReservePeople())
                .store(store)
                .workingUUID(workingUUID)
                .waitingUUID(waitingUUID)
                .expiredTime(dto.getExpireTime())
                .build();
        reserveRepository.save(reserve);

        // 3. 예약 접속 큐, 예약 대기 큐 생성
        redisUtil.create(workingUUID, reserve.getExpiredTime());
        log.info("Working Queue 생성됨: {}", workingUUID);
        redisUtil.create(waitingUUID, reserve.getExpiredTime());
        log.info("Waiting Queue 생성됨: {}", waitingUUID);
        return CreateReserveRes.builder().reserveIdx(reserve.getIdx()).build();
    }

    public String enroll(HttpServletResponse res, CustomUserDetails customUserDetails, Long reserveIdx) throws BaseException {
        // 1. 예외: 기업 회원이 아닐 때, 스토어 조회 안될 때, 스토어의 이메일과 인증된 사용자의 이메일이 다를 때
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.RESERVE_ENROLL_FAIL_NOT_FOUND)
        );
        /*
            if: 만약 예약자수보다 redis의 값의 크기가 적으면 예약 redis로 이동하거나 취소처리로 자리가 났을때
            else: 예약 접속 큐가 꽉차면 예약 대기 큐로 이동
         */
        String workingUUID = reserve.getWorkingUUID();
        String waitingUUID = reserve.getWaitingUUID();
        String userId = customUserDetails.getUserId();
        Long currentOrder = redisUtil.getOrder(workingUUID,userId);
        // if: 예약 접속 큐 등록 및 토큰 발급: 만약 예약자수보다 redis의 크기값이 적거나, 현재 working 큐에 있을때(사이트 재접속)
        if(reserve.getReservePeople() > redisUtil.getSize(workingUUID) || currentOrder != null){
            // 예약 redis의 자리가 있을때
            if(redisUtil.getOrder(workingUUID, userId) == null){
                redisUtil.save(workingUUID, userId, System.currentTimeMillis());
            }
             // 예약 페이지에 접근할 수 있는 accessToken 만들어줌
            String token = jwtUtil.createReserveToken(reserveIdx, userId);
            res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            Cookie aToken = new Cookie("WTOKEN", token);
            aToken.setHttpOnly(true);
            aToken.setSecure(true);
            aToken.setPath("/");
            aToken.setMaxAge(60 * 1);
            res.addCookie(aToken);
            // 예약 굿즈 구매 사이트로 이동 리다이렉션? 토큰이 없는 유저가 예약굿즈 구매 접근시 뒤로 보내야됨
            long rank = redisUtil.getOrder(workingUUID, userId) + 1L;
            return "팝업 스토어 예약 굿즈 페이지로 이동합니다 -> 접속 번호" + rank;
        }  else { // 예약 redis가 꽉차면 대기 redis로 이동
            redisUtil.save(waitingUUID, userId, System.currentTimeMillis());
            long rank = redisUtil.getOrder(waitingUUID, userId) + 1L;
            return "예약 대기: "+rank;
        }
    }

    public String cancel(String userId, Long reserveIdx) throws BaseException {
        Optional<Reserve> result = reserveRepository.findById(reserveIdx);
        if (result.isPresent()) {
            Reserve reserve = result.get();
            String onDoingUUID = reserve.getWorkingUUID();
            String waitingUUID = reserve.getWaitingUUID();
            // 현재 사용자 예약 접속 redis에서 삭제
            redisUtil.remove(onDoingUUID, userId);
            // 대기1순위 사용자 예약 접속 redis에 추가
            String movedUser = redisUtil.moveFirstWaitingToReserve(onDoingUUID, waitingUUID);
            if (movedUser != null) { return "대기자에서 예약자로 이동: " + movedUser; }
            return "대기자가 없습니다.";
        } else {
            throw new BaseException(BaseResponseMessage.RESERVE_CANCEL_FAIL);
        }
    }

    public String reserveStatus(String onDoingUUID, String waitingUUID){
        String reserveTotal = redisUtil.getAllValues(onDoingUUID);
        String reserveWaitingTotal = redisUtil.getAllValues(waitingUUID);
        return " 예약접속자 " + reserveTotal + " 예약대기자 " + reserveWaitingTotal;
    }
}

