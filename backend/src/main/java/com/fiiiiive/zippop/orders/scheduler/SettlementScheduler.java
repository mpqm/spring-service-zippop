package com.fiiiiive.zippop.orders.scheduler;

import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.Settlement;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.orders.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* 팝업 스토어 판매 수익금 정산 스케줄러 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementScheduler {
    private final OrdersRepository ordersRepository;
    private final SettlementRepository settlementRepository;

//    @Scheduled(cron = "0 0 9 * * ?")
    @Scheduled(fixedRate = 1000)
    public void createSettlement() {

        log.info("스케줄러 실행 시작 : 팝업 스토어 별 판매 금액 정산");
        // 어제 날짜 기준으로 조회
        List<Orders> ordersList = ordersRepository.findByStatusAndUpdatedAt("_DELIVERY", LocalDate.now());

        // 2. 스토어별 매출 계산
        Map<Long, Integer> storeRevenueMap = new HashMap<>();
        for (Orders order : ordersList) {
            storeRevenueMap.merge(order.getStoreIdx(), order.getTotalPrice(), Integer::sum);
        }

        // 3. Settlement 저장
        for (Map.Entry<Long, Integer> entry : storeRevenueMap.entrySet()) {
            Long storeIdx = entry.getKey();
            Integer totalRevenue = entry.getValue();

            Settlement settlement = Settlement.builder()
                    .storeIdx(storeIdx)
                    .totalRevenue(totalRevenue)
                    .settlementDate(LocalDate.now().minusDays(1))
                    .status("COMPLETE")
                    .build();

            settlementRepository.save(settlement);
            log.info("정산 생성 완료 - 스토어 ID: {}, 정산 금액: {}", storeIdx, totalRevenue);
        }
    }
}
