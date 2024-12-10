package com.fiiiiive.zippop.reserve.scheduler;

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

/* 예약 데이터를 기반으로 Redis 큐를 조작하는 스케줄러 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveScheduler {

    private final ReserveRepository reserveRepository;
    private final RedisUtil redisUtil;

    /* 매일 아침 9시에 예약 데이터를 순회하여 Redis 큐를 생성 */
    @Scheduled(cron = "0 0 9 * * ?")
    public void createReserveQueue() {
        log.info("스케줄러 실행 시작: 예약 큐 생성 시작");

        // 오늘 날짜 기준으로 예약 데이터를 조회
        List<Reserve> reserveList = reserveRepository.findAllByStartDate(LocalDate.now());

        for (Reserve reserve : reserveList) {
            try {
                // Working Queue 식별자(UUID)
                String workingUUID = reserve.getWorkingUUID();

                // Waiting Queue 식별자(UUID)
                String waitingUUID = reserve.getWaitingUUID();

                // TTL: 시작 시간과 종료 시간의 차이 초 계산
                long ttl = Duration.between(reserve.getStartTime(), reserve.getEndTime()).getSeconds();

                // Working Queue 확인 및 생성
                if(!redisUtil.exists(workingUUID)) redisUtil.create(workingUUID, ttl);

                // Wating Queue 확인 및 생성
                if(!redisUtil.exists(waitingUUID)) redisUtil.create(waitingUUID, ttl);

            } catch (Exception e) {
                log.error("레디스 큐 생성 중 오류 발생 - 예약 ID: {}, 오류: {}", reserve.getIdx(), e.getMessage());
            }
        }

        log.info("스케줄러 실행 완료: 예약 큐 생성 종료.");
    }
}
