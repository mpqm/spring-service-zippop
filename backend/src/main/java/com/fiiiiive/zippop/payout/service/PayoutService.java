package com.fiiiiive.zippop.payout.service;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.orders.model.Orders;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.payout.model.Payout;
import com.fiiiiive.zippop.payout.repository.PayoutRepository;
import com.fiiiiive.zippop.popup.model.Popup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {
    private final PayoutRepository payoutRepository;
    private final OrdersRepository ordersRepository;



    @Transactional
    public void createDailyPayout() {
        LocalDate targetDate = LocalDate.now().minusDays(1);

        log.info("[PayoutService] {} 기준 정산 시작", targetDate);

        // 어제 날짜 기준으로 조회
        List<Orders> ordersList =
                ordersRepository.findByStatusAndUpdatedAt(
                        OrdersStatus.STOCK_DELIVERY.getName(),
                        OrdersStatus.RESERVE_DELIVERY.getName(),
                        targetDate
                );

        if (ordersList.isEmpty()) {
            log.info("정산 대상 주문 없음");
            return;
        }

        // Orders.popup(ManyToOne) 기준 팝업별 매출 합계 — 이미 fetch된 popup 사용
        Map<Long, Integer> popupRevenueMap = ordersList.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getPopup().getIdx(),
                        Collectors.summingInt(Orders::getTotalPrice)
                ));

        for (Map.Entry<Long, Integer> entry : popupRevenueMap.entrySet()) {
            Long popupIdx = entry.getKey();
            Integer revenue = entry.getValue();

            Popup popup = ordersList.stream()
                    .filter(o -> o.getPopup().getIdx().equals(popupIdx))
                    .findFirst()
                    .map(Orders::getPopup)
                    .orElseThrow(() -> new BaseException(BaseMessage.STORE_SEARCH_FAIL_NOT_FOUND));

            if (payoutRepository.existsByPopupIdxAndPayoutDate(popupIdx, targetDate)) {
                log.info("이미 정산 존재 - popupIdx: {}", popupIdx);
                continue;
            }

            Payout payout = Payout.create(popup, revenue, targetDate);
            payoutRepository.save(payout);
            log.info("정산 생성 완료 - popupIdx: {}, 금액: {}", popupIdx, revenue);
        }
    }

}
