package com.fiiiiive.zippop.popup.service;

import com.fiiiiive.zippop.account.model.entity.Company;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.global.enums.PopupStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.popup.model.dto.*;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.entity.PopupLike;
import com.fiiiiive.zippop.popup.model.entity.PopupReview;
import com.fiiiiive.zippop.popup.repository.PopupLikeRepository;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.popup.repository.PopupReviewRepository;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveRes;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.*;

// Popup 비즈니스 로직 서비스
@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final PopupLikeRepository popupLikeRepository;
    private final CompanyRepository companyRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final PopupReviewRepository popupReviewRepository;
    private final ReserveRepository reserveRepository;

    // 팝업 등록
    @Transactional
    public void createPopup(CustomUserDetails user, CreatePopupReq req, List<String> urls) throws ServiceException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_REGISTER_FAIL_UNAUTHORIZED)
        );

        // Popup 생성
        Popup popup = Popup.create(
                user.getEmail(),
                req.getPopupName(),
                req.getPopupContent(),
                req.getPopupAddress(),
                req.getCategory(),
                req.getTotalPeople(),
                req.getPopupStartDate(),
                req.getPopupEndDate(),
                company
        );
        popupRepository.save(popup);

        // Popup Image 생성
        popup.addImages(urls);

    }

    // 팝업 조회
    public GetPopupRes getPopup(Long popupIdx) throws ServiceException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_SEARCH_FAIL_NOT_FOUND)
        );

        return popup.toDto();

    }

    // 팝업 목록 조회
    public Page<GetPopupRes> getPopups(String status, String keyword, int page, int size) throws ServiceException {

        // 팝업 페이지 조회(keyword, status, pageable) 조회
        // true : 검색어가 있는 경우, 상태에 따라 활성화된 또는 종료된 팝업을 검색어로 페이징 조회
        // false : 검색어가 없는 경우, 상태에 따라 활성화 또는 종료된 팝업을 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Popup> popupPage = (keyword != null)
                ? popupRepository.findAllByKeywordAndStatus(keyword, PopupStatus.valueOf(status), pageable)
                : popupRepository.findAllByStatus(PopupStatus.valueOf(status), pageable);

        // DTO 반환 (데이터가 없어도 빈 페이지 반환)
        return Popup.toDtoPage(popupPage);

    }

    // 팝업 수정
    @Transactional
    public void updatePopup(CustomUserDetails user, Long popupIdx, UpdatePopupReq req, List<String> urls) throws ServiceException {

        // 팝업 조회(popupIdx, email)
        Popup popup = popupRepository.findByPopupIdxAndCompanyEmail(popupIdx, user.getEmail()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_UPDATE_FAIL_NOT_FOUND)
        );

        // 팝업 수정
        popup.update(req);

        // 팝업 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지 저장
        if (urls != null) {
            popup.replaceImages(urls);
        }

    }

    // 팝업 삭제
    @Transactional
    public void deletePopup(CustomUserDetails user, Long popupIdx) throws ServiceException{

        // 팝업 조회(popupIdx, email)
        Popup popup = popupRepository.findByPopupIdxAndCompanyEmail(popupIdx, user.getEmail()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_DELETE_FAIL_NOT_FOUND)
        );

        // 팝업 소프트 삭제
        popup.endPopup();

    }

    // 팝업 좋아요 증감
    @Transactional
    public void togglePopupLike(CustomUserDetails user, Long popupIdx) throws ServiceException {

        // 팝업 인덱스로 조회 없으면 예외 반환
        Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_LIKE_FAIL_NOT_FOUND)
        );

        // 좋아요 증감
        // if : 이미 좋아요를 누른 상태면 좋아요 삭제 / 팝업 좋아요 개수 감소(직접 쿼리 활용)
        // else : 좋아요를 처음 누르면 좋아요 저장 / 팝업 좋아요 개수 증가(직접 쿼리 활용)
        Optional<PopupLike> popupLike = popupLikeRepository.findByCustomerIdxAndPopupIdx(user.getIdx(), popupIdx);
        if (popupLike.isPresent()) {
            popup.decreaseLike();
        } else {
            popupLikeRepository.save(PopupLike.create(popup, user.getIdx()));
            popup.increaseLike();
        }

    }

    // 팝업 좋아요 목록 조회(고객용)
    public Page<GetPopupRes> getMyLikedPopups(CustomUserDetails user, int page, int size) throws ServiceException {

        // 팝업 페이지 조회(customerIdx)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<PopupLike> popupLikePage = popupLikeRepository.findAllByCustomerIdx(user.getIdx(), pageable);

        return PopupLike.toDtoPage(popupLikePage);

    }

    // 팝업 리뷰 등록
    @Transactional
    public void createPopupReview(CustomUserDetails user, Long popupIdx, CreatePopupReviewReq req) throws ServiceException {

        // 결제 조회(popupIdx, customerIdx, 결제 완료 상태) / 결제한 사람만 리뷰 작성 가능
        ordersDetailRepository.existsReviewableOrder(user.getIdx(), popupIdx, List.of(OrdersStatus.STOCK_COMPLETE, OrdersStatus.RESERVE_COMPLETE)).orElseThrow(() ->
                new ServiceException(ServiceErrorCode.STORE_REVIEW_FAIL_INVALID_MEMBER)
        );

        // 팝업 조회 (popupIdx)
        Popup popup = popupRepository.findById(popupIdx).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_REVIEW_FAIL_NOT_FOUND)
        );

        // 팝업 리뷰 조회(popupIdx, customerIdx) / 팝업 하나당 한개의 리뷰 작성 가능
        Optional<PopupReview> popupReviewOpt = popupReviewRepository.findByPopupIdxAndCustomerIdx(popupIdx, user.getIdx());
        if(popupReviewOpt.isPresent()) {
            throw new ServiceException(ServiceErrorCode.STORE_REVIEW_FAIL_DUPLICATED);
        }

        PopupReview popupReview = PopupReview.create(
                user.getIdx(),
                user.getName(),
                user.getEmail(),
                popup,
                req.getReviewTitle(),
                req.getReviewContent(),
                req.getReviewRating()
        );

        popupReviewRepository.save(popupReview);

    }

    // 팝업 리뷰 목록 조회
    public Page<GetPopupReviewRes> getPopupReviews(Long popupIdx, int page, int size) throws ServiceException {

        // 리뷰 조회(popupIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<PopupReview> popupReviewPage = popupReviewRepository.findAllByPopupIdx(popupIdx, pageable);
        return PopupReview.toDtoPage(popupReviewPage);

    }

    // 팝업 리뷰 목록 조회(고객용)
    public Page<GetPopupReviewRes> getMyReviews(CustomUserDetails user, int page, int size) throws ServiceException {

        // 리뷰 목록 조회(customerIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<PopupReview> popupReviewPage = popupReviewRepository.findAllByCustomerIdx(user.getIdx(), pageable);

        return PopupReview.toDtoPage(popupReviewPage);

    }

    public void updatePopupStatus(){
        List<Popup> popupList = popupRepository.findAllByEndDate(LocalDate.now());

        for (Popup popup : popupList) {
            popup.endPopup();
        }
    }

    // 예약 목록 조회
    public Page<GetReserveRes> getPopupReserves(Long popupIdx, String keyword, int page, int size) throws ServiceException {

        // 예약 조회(status, popupIdx, keyword)
        Page<Reserve> reservePage;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if(popupIdx == null){
            if(keyword == null) reservePage = reserveRepository.findAllByStatus(PopupStatus.POPUP_START, pageable);
            else reservePage = reserveRepository.findAllByKeywordAndStatus(keyword, PopupStatus.POPUP_START, pageable);
        } else {
            reservePage = reserveRepository.findAllByPopupIdx(popupIdx, PopupStatus.POPUP_START, pageable);
        }

        return Reserve.toDtoPage(reservePage);
    }

}
