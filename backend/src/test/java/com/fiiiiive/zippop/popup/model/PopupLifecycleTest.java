package com.fiiiiive.zippop.popup.model;

import com.fiiiiive.zippop.global.api.ServiceErrorCode;
import com.fiiiiive.zippop.global.api.ServiceException;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.model.enums.GoodsStatus;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.dto.CreatePopupReq;
import com.fiiiiive.zippop.popup.model.dto.UpdatePopupReq;
import com.fiiiiive.zippop.popup.model.enums.PopupStatus;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class PopupLifecycleTest {
    private final LocalDate today = LocalDate.now();

    @Test void reversedPopupDatesAreRejectedByRequestValidation() {
        CreatePopupReq request = CreatePopupReq.builder()
                .popupName("Popup")
                .popupContent("Content")
                .popupAddress("Address")
                .category("Food")
                .totalPeople(1)
                .popupStartDate(today.plusDays(1))
                .popupEndDate(today)
                .build();
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(request))
                    .anySatisfy(violation -> assertThat(violation.getPropertyPath().toString()).isEqualTo("validDateRange"));
        }
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
        assertThatThrownBy(() -> popup.update(request))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.STORE_UPDATE_FAIL_INVALID_PERIOD));
    }
}
