package com.fiiiiive.zippop.popup.service;

import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.api.ServiceErrorCode;
import com.fiiiiive.zippop.global.api.ServiceException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.enums.GoodsStatus;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.popup.model.dto.CreatePopupReviewReq;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.entity.PopupLike;
import com.fiiiiive.zippop.popup.model.enums.PopupStatus;
import com.fiiiiive.zippop.popup.repository.PopupLikeRepository;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.popup.repository.PopupReviewRepository;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopupServiceTest {

    @Mock PopupRepository popupRepository;
    @Mock PopupLikeRepository popupLikeRepository;
    @Mock CompanyRepository companyRepository;
    @Mock OrdersDetailRepository ordersDetailRepository;
    @Mock PopupReviewRepository popupReviewRepository;
    @Mock ReserveRepository reserveRepository;

    private PopupService popupService;
    private CustomUserDetails user;

    @Test void overdueActivePopupsAreEndedAndTheirGoodsBecomeStock() {
        var goods = com.fiiiiive.zippop.goods.model.entity.Goods.builder()
                .status(GoodsStatus.GOODS_RESERVED).build();
        Popup popup = Popup.builder().status(PopupStatus.POPUP_START)
                .goodsList(java.util.List.of(goods)).build();
        when(popupRepository.findAllByStatusAndEndDateBefore(
                PopupStatus.POPUP_START, java.time.LocalDate.now()))
                .thenReturn(java.util.List.of(popup));
        popupService.updatePopupStatus();
        assertThat(popup.getStatus()).isEqualTo(PopupStatus.POPUP_END);
        assertThat(goods.getStatus()).isEqualTo(GoodsStatus.GOODS_STOCK);
    }

    @BeforeEach
    void setUp() {
        popupService = new PopupService(
                popupRepository,
                popupLikeRepository,
                companyRepository,
                ordersDetailRepository,
                popupReviewRepository,
                reserveRepository
        );
        user = CustomUserDetails.builder()
                .accountId(99L).customerId(3L)
                .email("user@test.com")
                .name("사용자")
                .role("ROLE_CUSTOMER")
                .build();
    }

    @Test
    void togglingAnExistingLikeDeletesTheRelationAndDecrementsOnce() {
        Popup popup = Popup.builder().idx(7L).likeCount(1).build();
        PopupLike popupLike = PopupLike.builder().idx(8L).popup(popup).build();
        when(popupRepository.findForUpdate(7L)).thenReturn(Optional.of(popup));
        when(popupLikeRepository.findByCustomerIdxAndPopupIdx(3L, 7L)).thenReturn(Optional.of(popupLike));

        popupService.togglePopupLike(user, 7L);

        verify(popupLikeRepository).delete(popupLike);
        assertThat(popup.getLikeCount()).isZero();
    }

    @Test
    void reviewRequiresACompletedOrder() {
        CreatePopupReviewReq request = CreatePopupReviewReq.builder()
                .reviewTitle("후기")
                .reviewContent("내용")
                .reviewRating(5)
                .build();
        when(ordersDetailRepository.existsReviewableOrder(org.mockito.ArgumentMatchers.eq(3L), org.mockito.ArgumentMatchers.eq(7L), anyList()))
                .thenReturn(false);

        assertThatThrownBy(() -> popupService.createPopupReview(user, 7L, request))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.STORE_REVIEW_FAIL_INVALID_MEMBER));
        verify(popupRepository, never()).findById(7L);
    }
}
