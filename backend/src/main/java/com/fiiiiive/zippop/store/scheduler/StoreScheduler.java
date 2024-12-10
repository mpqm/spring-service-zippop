package com.fiiiiive.zippop.store.scheduler;

import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


/* 팝업스토어가 종료되면 재고굿즈를 팔기위해 상태를 STORE_END로 변경하는 스케줄러 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreScheduler {

    private final StoreRepository storeRepository;

//    @Scheduled(cron = "0 0 9 * * ?")
    @Scheduled(fixedRate = 1000)
    public void updateStoreStatus() {
        log.info("스케줄러 실행 시작 - 팝업스토어 상태 변경 시작");
        // 오늘 날짜 기준으로 스토어 조회
        List<Store> storeList = storeRepository.findAllByEndDate(LocalDate.now());

        for(Store store : storeList) {
            try {
                store.setStatus("STORE_END");
                storeRepository.save(store);
            }catch (Exception e) {
                log.error("팝업 스토어 상태 변경 중 오류 발생 - 스토어 ID : {}, 오류: { }", store.getIdx() );
            }
        }

        log.info("스케줄러 실행 종료 - 팝업 스토어 상태 변경 완료");
    }
}
