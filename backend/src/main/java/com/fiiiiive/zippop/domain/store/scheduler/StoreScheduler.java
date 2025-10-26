package com.fiiiiive.zippop.domain.store.scheduler;

import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.domain.goods.entity.Goods;
import com.fiiiiive.zippop.domain.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.domain.store.entity.Store;
import com.fiiiiive.zippop.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class StoreScheduler {

    private final StoreRepository storeRepository;
    private final GoodsRepository goodsRepository;

    // 스토어가 종료되면 재고굿즈를 팔기위해 스토어 상태를 STORE_END와 굿즈 상태를 GOODS_STOCK으로 변경하는 스케줄러
    @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시
    public void updateStoreStatus() {
        try {
            log.info("[팝업스토어 상태 변경 스케줄러]");

            // 오늘 날짜 기준으로 스토어 조회
            List<Store> storeList = storeRepository.findAllByEndDate(LocalDate.now());

            // 스토어 목록 순회
            for(Store store : storeList) {

                // 스토어의 굿즈를 순회하며 GOODS_STOCK 으로 변경
                for(Goods goods : store.getGoodsList()){
                    goods.setStatus(BaseStatus.GOODS_STOCK);
                    goodsRepository.save(goods);
                }

                // 스토어 저장
                store.setStatus(BaseStatus.STORE_END);
                storeRepository.save(store);
            }
        } catch (Exception e) {
            log.error("스토어 상태 변경 스케줄러 실행 중 오류 발생: {}", e.getMessage());
        }
    }
}
