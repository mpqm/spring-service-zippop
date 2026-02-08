package com.fiiiiive.zippop.payout.service;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.Orders;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.payout.model.PayoutDto;
import com.fiiiiive.zippop.payout.model.Payout;
import com.fiiiiive.zippop.payout.repository.PayoutRepository;
import com.fiiiiive.zippop.popup.model.Popup;
import com.fiiiiive.zippop.popup.policy.PopupPolicy;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PopupRepository popupRepository;
    private final PayoutRepository payoutRepository;
    private final OrdersRepository ordersRepository;
    private final PopupPolicy popupPolicy;



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

        Map<Long, Integer> popupRevenueMap = ordersList.stream()
                .collect(Collectors.groupingBy(
                        Orders::getPopupIdx,
                        Collectors.summingInt(Orders::getTotalPrice)
                ));

        for (Map.Entry<Long, Integer> entry : popupRevenueMap.entrySet()) {
            Long popupIdx = entry.getKey();
            Integer revenue = entry.getValue();

            Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow();

            boolean exists = payoutRepository.existsByPopupIdxAndPayoutDate(popupIdx, targetDate);
            if (exists) {
                log.info("이미 정산 존재 - popupIdx: {}", popupIdx);
                continue;
            }

            Payout payout = Payout.create(popup, revenue, targetDate);
            payoutRepository.save(payout);

            log.info("정산 생성 완료 - popupIdx: {}, 금액: {}", popupIdx, revenue);

            payoutRepository.save(payout);
        }
    }

}
