package com.fiiiiive.zippop.global.validation;

import com.fiiiiive.zippop.goods.model.dto.CreateGoodsReq;
import com.fiiiiive.zippop.account.model.dto.CreateAccountReq;
import com.fiiiiive.zippop.orders.model.dto.CreateOrdersReq;
import com.fiiiiive.zippop.popup.model.dto.CreatePopupReq;
import com.fiiiiive.zippop.popup.model.dto.UpdatePopupReq;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveReq;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationContractTest {

    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    @AfterAll
    static void closeValidatorFactory() {
        FACTORY.close();
    }

    @Test
    void goodsAndPopupRequestsUseTypeCompatibleConstraints() {
        CreateGoodsReq goods = CreateGoodsReq.builder()
                .popupIdx(1L)
                .goodsName("굿즈")
                .goodsPrice(1000)
                .goodsAmount(10)
                .build();
        CreatePopupReq createPopup = CreatePopupReq.builder()
                .popupName("팝업")
                .popupAddress("서울")
                .popupContent("설명")
                .category("라이프스타일")
                .totalPeople(100)
                .popupStartDate(LocalDate.now())
                .popupEndDate(LocalDate.now().plusDays(1))
                .build();
        UpdatePopupReq updatePopup = UpdatePopupReq.builder()
                .popupName("팝업")
                .popupAddress("서울")
                .popupContent("설명")
                .category("라이프스타일")
                .totalPeople(100)
                .popupStartDate(LocalDate.now())
                .popupEndDate(LocalDate.now().plusDays(1))
                .build();

        assertThat(VALIDATOR.validate(goods)).isEmpty();
        assertThat(VALIDATOR.validate(createPopup)).isEmpty();
        assertThat(VALIDATOR.validate(updatePopup)).isEmpty();
    }

    @Test
    void paymentAndReserveRequestsRejectMissingOrInvalidRequiredValues() {
        CreateOrdersReq invalidOrder = CreateOrdersReq.builder().build();
        CreateReserveReq invalidReserve = CreateReserveReq.builder()
                .popupIdx(1L)
                .reservePeople(0)
                .reserveStartTime(LocalDateTime.now().plusDays(1))
                .reserveEndTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();

        assertThat(VALIDATOR.validate(invalidOrder))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("impUid", "merchantUid", "popupIdx");
        assertThat(VALIDATOR.validate(invalidReserve))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("reservePeople");
    }

    @Test
    void signupPasswordMatchesTheEightToSeventyTwoCharacterPolicy() {
        CreateAccountReq shortPassword = CreateAccountReq.builder()
                .role(com.fiiiiive.zippop.account.model.enums.RoleType.ROLE_CUSTOMER).userId("member01").email("member@example.test")
                .password("1234567").name("Member").phoneNumber("01012345678").address("Seoul")
                .build();
        CreateAccountReq validPassword = CreateAccountReq.builder()
                .role(com.fiiiiive.zippop.account.model.enums.RoleType.ROLE_CUSTOMER).userId("member01").email("member@example.test")
                .password("12345678").name("Member").phoneNumber("01012345678").address("Seoul")
                .build();

        assertThat(VALIDATOR.validate(shortPassword))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("password");
        assertThat(VALIDATOR.validate(validPassword)).isEmpty();
    }
}
