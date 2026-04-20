package com.fiiiiive.zippop.popup.service;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.GetOrdersRes;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.payout.model.dto.GetPopupPayoutsRes;
import com.fiiiiive.zippop.payout.model.entity.Payout;
import com.fiiiiive.zippop.payout.repository.PayoutRepository;
import com.fiiiiive.zippop.popup.model.dto.GetPopupRes;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.policy.PopupPolicy;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveRes;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

// 기업용 Popup 조회 서비스
@Service
@RequiredArgsConstructor
public class CompanyPopupService {

    private final PopupRepository popupRepository;
    private final PayoutRepository payoutRepository;
    private final ReserveRepository reserveRepository;
    private final PopupPolicy popupPolicy;
    private final OrdersRepository ordersRepository;

    // 팝업 목록 조회(기업용)
    public Page<GetPopupRes> getCompanyPopups(CustomUserDetails user, String keyword, int page, int size) throws BaseException {

        // 팝업 페이지 조회(keyword, email, pageable) 조회
        // true : 키워드(keyword)가 있는 경우, 등록된 기업회원의 이메일과 키워드로 페이징 조회
        // false : 키워드(keyword)가 없는 경우, 등록된 기업회원의 이메일로 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Popup> popupPage = (keyword != null)
                ? popupRepository.findAllByKeywordAndCompanyEmail(keyword, user.getEmail(), pageable)
                : popupRepository.findAllByCompanyEmail(user.getEmail(), pageable);

        return Popup.toDtoPage(popupPage);

    }

    // 기업 정산 금액 조회
    public Page<GetPopupPayoutsRes> getCompanyPopupPayouts(CustomUserDetails user, Long popupIdx, int page, int size) throws BaseException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findByPopupIdx(popupIdx)
                .orElseThrow(() -> new BaseException(BaseMessage.PAYOUT_SEARCH_FAIL_NOT_FOUND_STORE));

        popupPolicy.validateOwner(popup, user);

        // 팝업 페이지 조회(popupIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Payout> payoutPage = payoutRepository.findAllByPopupIdx(popupIdx, pageable);

        return Payout.toDtoPage(payoutPage);

    }

    // 예약 목록 조회(기업용)
    public Page<GetReserveRes> getCompanyPopupReserves(CustomUserDetails user, Long popupIdx, int page, int size) throws BaseException {

        Page<Reserve> reservePage = reserveRepository.findAllByCompanyEmail(popupIdx, user.getEmail(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Reserve.toDtoPage(reservePage);
    }

    // 기업 고객 주문 목록 조회
    public Page<GetOrdersRes> getCompanyPopupOrdersList(CustomUserDetails user, Long popupIdx, int page, int size) throws BaseException {

        // 팝업(popupIdx) 조회
        Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND_STORE)
        );

        // 팝업 소유 확인
        if(!(popup.getCompanyEmail().equals(user.getEmail()))) {
            throw new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_INVALID_MEMBER);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = ordersRepository.findAllByPopupIdx(popupIdx, pageable).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Orders.toDtoPage(ordersPage);

    }

    // 기업 고객 주문 상세 조회
    public GetOrdersRes getPopupOrdersDetail(CustomUserDetails user, Long popupIdx, Long ordersIdx) throws BaseException {

        // 팝업(popupIdx) 조회
        Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        // 팝업 소유 확인
        if(!(popup.getCompanyEmail().equals(user.getEmail()))) {
            throw new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_INVALID_MEMBER);
        }

        Orders orders = ordersRepository.findByOrdersIdxAndPopupIdx(ordersIdx, popupIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        return orders.toDto();
    }

}
