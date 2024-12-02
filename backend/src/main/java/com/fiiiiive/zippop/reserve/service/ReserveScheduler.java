package com.fiiiiive.zippop.reserve.service;

import com.fiiiiive.zippop.global.utils.RedisUtil;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveScheduler {

    private final ReserveRepository reserveRepository;
    private final RedisUtil redisUtil;

    /**
     * 매일 아침 9시에 예약 데이터 순회 후 Redis 큐 생성
     */
//    @Scheduled(fixedRate = 10000)
 @Scheduled(cron = "0 0 9 * * ?") // 매일 9시
    public void processReserveQueues() {
        log.info("스케줄러 실행 시작: 예약 데이터를 처리합니다.");

        // 오늘 기준 모든 예약 데이터 조회
        LocalDate today = LocalDate.now();
        List<Reserve> reserveList = reserveRepository.findAllByStartDate(today);

        for (Reserve reserve : reserveList) {
            try {
                String workingUUID = reserve.getWorkingUUID();
                String waitingUUID = reserve.getWaitingUUID();

                // TTL 계산
                long ttl = Duration.between(reserve.getStartTime(), reserve.getEndTime()).getSeconds();
                boolean aa = redisUtil.exists(workingUUID);
                System.out.println(aa);
                // Working Queue
                if (!redisUtil.exists(workingUUID) || redisUtil.getTTL(workingUUID) <= 0) {
                    redisUtil.create(workingUUID, ttl);
                    log.info("Working Queue 생성 완료: {}", workingUUID);
                } else {
                    log.info("Working Queue 이미 존재 (TTL: {}): {}", redisUtil.getTTL(workingUUID), workingUUID);
                }

                // Waiting Queue
                if (!redisUtil.exists(waitingUUID) || redisUtil.getTTL(waitingUUID) <= 0) {
                    redisUtil.create(waitingUUID, ttl);
                    log.info("Waiting Queue 생성 완료: {}", waitingUUID);
                } else {
                    log.info("Waiting Queue 이미 존재 (TTL: {}): {}", redisUtil.getTTL(waitingUUID), waitingUUID);
                }
            } catch (Exception e) {
                log.error("레디스 큐 생성 중 오류 발생 - 예약 ID: {}, 오류: {}", reserve.getIdx(), e.getMessage());
            }
        }

        log.info("스케줄러 실행 완료: 예약 데이터 처리 종료.");
    }
}