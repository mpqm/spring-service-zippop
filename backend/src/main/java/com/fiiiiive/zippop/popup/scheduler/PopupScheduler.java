package com.fiiiiive.zippop.popup.scheduler;

import com.fiiiiive.zippop.popup.service.PopupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class PopupScheduler {

    private final PopupService popupService;

    // 팝업이 종료되면 재고굿즈를 팔기위해 팝업 상태를 STORE_END와 굿즈 상태를 GOODS_STOCK으로 변경하는 스케줄러
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void updatePopupStatus() {

        popupService.updatePopupStatus();

    }

}
