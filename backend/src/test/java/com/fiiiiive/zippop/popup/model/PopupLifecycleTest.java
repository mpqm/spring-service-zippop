package com.fiiiiive.zippop.popup.model;

import com.fiiiiive.zippop.global.enums.*;
import com.fiiiiive.zippop.global.base.InputValidationException;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.dto.UpdatePopupReq;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class PopupLifecycleTest {
    private final LocalDate today = LocalDate.now();

    @Test void reversedPopupDatesAreRejected() {
        assertThatThrownBy(() -> Popup.create("company@example.test", "Popup", "Content", "Address", "Food",
                1, today.plusDays(1), today, null)).isInstanceOf(InputValidationException.class);
    }

    @Test void zeroRemainingCapacityIsAValidPersistedValue() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validateProperty(Popup.builder().totalPeople(0).build(), "totalPeople")).isEmpty();
        }
    }

    @Test void goodsCreatedAfterPopupEndsAreStockGoods() {
        Goods goods = Goods.create("Goods", 1, 1000, "Content", Popup.builder().status(PopupStatus.POPUP_END).build());
        assertThat(goods.getStatus()).isEqualTo(GoodsStatus.GOODS_STOCK);
    }

    @Test void popupDateChangesMustStillContainExistingReservations() {
        Reserve reserve = Reserve.builder().startTime(today.plusDays(1).atStartOfDay())
                .endTime(today.plusDays(2).atStartOfDay()).build();
        Popup popup = Popup.builder().reserveList(List.of(reserve)).build();
        var request = UpdatePopupReq.builder().popupStartDate(today).popupEndDate(today).build();
        assertThatThrownBy(() -> popup.update(request)).isInstanceOf(InputValidationException.class);
    }
}
