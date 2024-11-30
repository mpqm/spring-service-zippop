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

    private void issueWToken(HttpServletResponse res, Long reserveIdx, String userId) {
        String token = jwtUtil.createReserveToken(reserveIdx, userId);
        res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Cookie aToken = new Cookie("WTOKEN", token);
        aToken.setHttpOnly(true);
        aToken.setSecure(true);
        aToken.setPath("/");
        aToken.setMaxAge(60 * 1); // 1분간 유효
        res.addCookie(aToken);
    }
    private void deleteWToken(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("WTOKEN", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);  // 쿠키 만료 처리
        response.addCookie(deleteCookie);
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

        // 예약 접속 큐의 현재 크기 및 사용자의 순서 조회
        Long currentOrder = redisUtil.getOrder(workingUUID,userId);
        Long workingQueueSize = redisUtil.getSize(workingUUID);
        // 예약 접속 큐에 사용자가 이미 있는 경우 -> 사이트 재접속

        if (currentOrder != null) {
            // 이미 등록되어 있으므로 토큰을 발급합니다.
            issueWToken(res, reserveIdx, userId);
            long rank = currentOrder + 1; // 0부터 시작하므로 +1 해서 순위를 반환
            return "팝업 스토어 예약 굿즈 페이지로 이동합니다 -> 접속 번호 " + rank;
        }
        // 예약 접속 큐에 자리가 있는 경우 (현재 예약자 수보다 큐의 크기가 작은 경우)
        if (workingQueueSize < reserve.getReservePeople()) {
            // 새로운 사용자 등록
            redisUtil.save(workingUUID, userId, System.currentTimeMillis());

            issueWToken(res, reserveIdx, userId);

            long rank = redisUtil.getOrder(workingUUID, userId) + 1;
            return "팝업 스토어 예약 굿즈 페이지로 이동합니다 -> 접속 번호 " + rank;
        }

        // 예약 접속 큐가 꽉 찬 경우 -> 대기 큐에 추가
        redisUtil.save(waitingUUID, userId, System.currentTimeMillis());
        long rank = redisUtil.getOrder(waitingUUID, userId) + 1;
        return "예약 대기: " + rank;
    }

    public String cancel(CustomUserDetails customUserDetails, Long reserveIdx) throws BaseException {
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.RESERVE_CANCEL_FAIL)
        );
        String workingUUID = reserve.getWorkingUUID();
        String waitingUUID = reserve.getWaitingUUID();
        Long currentOrder = redisUtil.getOrder(workingUUID, customUserDetails.getUserId());
        // 4. 현재 사용자가 예약 큐에 있는 경우
        if (currentOrder != null) {
            // 현재 사용자 예약 접속 redis에서 삭제
            redisUtil.remove(workingUUID, customUserDetails.getUserId());
            // 대기1순위 사용자 예약 접속 redis에 추가
            String firstWaitingUser = redisUtil.firstWatingUserToWorking(workingUUID, waitingUUID);
            if(firstWaitingUser != null ){
                log.info("대기자에서 예약자로 이동: {}", firstWaitingUser);
            } else {
                log.info("대기자가 없습니다");
            }
        } else {
            Long waitingOrder = redisUtil.getOrder(waitingUUID, customUserDetails.getUserId());
            if (waitingOrder != null) {
                // 대기 큐에서 현재 사용자 제거
                redisUtil.remove(waitingUUID, customUserDetails.getUserId());
                log.info("대기 예약이 취소되었습니다.");
            }
        }
        return "예약을 취소했습니다.";
    }

    public String reserveStatus(String onDoingUUID, String waitingUUID){
        String reserveTotal = redisUtil.getAllValues(onDoingUUID);
        String reserveWaitingTotal = redisUtil.getAllValues(waitingUUID);
        return " 예약접속자 " + reserveTotal + " 예약대기자 " + reserveWaitingTotal;
    }
}

