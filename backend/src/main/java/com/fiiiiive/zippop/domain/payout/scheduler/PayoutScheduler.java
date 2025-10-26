package com.fiiiiive.zippop.domain.payout.scheduler;

import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.domain.orders.entity.Orders;
import com.fiiiiive.zippop.domain.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.domain.payout.entity.Payout;
import com.fiiiiive.zippop.domain.payout.repository.PayoutRepository;
import com.fiiiiive.zippop.domain.store.entity.Store;
import com.fiiiiive.zippop.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutScheduler {

    private final OrdersRepository ordersRepository;
    private final PayoutRepository payoutRepository;
    private final StoreRepository storeRepository;

    // 팝업 스토어 판매 수익금 정산 스케줄러 (매일 00:00:00에 실행)
    @Scheduled(fixedRate = 1000000)
    public void createPayout() {
        try {
            log.info("[팝업 스토어 별 판매 금액 정산 스케줄러]");

            // 어제 날짜 기준으로 조회
            // List<Orders> ordersList = ordersRepository.findByStatusAndUpdatedAt(BaseStatus.valueOf("STOCK_DELIVERY"),BaseStatus.valueOf("RESERVE_DELIVERY"), LocalDate.now().minusDays(1));

            // 오늘 날짜 기준으로 조회(테스트용)
            List<Orders> ordersList = ordersRepository.findByStatusAndUpdatedAt(BaseStatus.valueOf("STOCK_DELIVERY"),BaseStatus.valueOf("RESERVE_DELIVERY"), LocalDate.now());

            // 스토어별 매출 계산
            Map<Long, Integer> storeRevenueMap = new HashMap<>();
            for (Orders order : ordersList) {
                storeRevenueMap.merge(order.getStoreIdx(), order.getTotalPrice(), Integer::sum);
            }

            // Payout 저장
            for (Map.Entry<Long, Integer> entry : storeRevenueMap.entrySet()) {
                Long storeIdx = entry.getKey();
                Integer totalRevenue = entry.getValue();

                // Store 조회
                Store store = storeRepository.findByStoreIdx(storeIdx).orElse(null);
                if (store == null) {
                    continue;
                }

                // 정산 금액 저장
                Payout payout = Payout.builder()
                        .store(store)
                        .totalRevenue(totalRevenue)
                        .payoutDate(LocalDate.now())
                        .status(BaseStatus.COMPLETE)
                        .build();
                payoutRepository.save(payout);
                log.info("정산 생성 완료 - 스토어 ID: {}, 정산 금액: {}", storeIdx, totalRevenue);
            }
        } catch (Exception e) {
            log.error("팝업 스토어 별 판매 금액 정산 중 오류 발생: {}", e.getMessage());
        }
    }
}
