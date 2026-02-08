package com.fiiiiive.zippop.popup.service;

import com.fiiiiive.zippop.account.model.Company;
import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.Orders;
import com.fiiiiive.zippop.orders.model.OrdersDetail;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.payout.model.Payout;
import com.fiiiiive.zippop.payout.model.PayoutDto;
import com.fiiiiive.zippop.payout.repository.PayoutRepository;
import com.fiiiiive.zippop.popup.model.PopupDto;
import com.fiiiiive.zippop.popup.model.Popup;
import com.fiiiiive.zippop.popup.model.PopupLike;
import com.fiiiiive.zippop.popup.model.PopupReview;
import com.fiiiiive.zippop.popup.policy.PopupPolicy;
import com.fiiiiive.zippop.popup.repository.PopupLikeRepository;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.popup.repository.PopupReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;

/**
 * Popup 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final PopupLikeRepository popupLikeRepository;
    private final CompanyRepository companyRepository;
    private final PayoutRepository payoutRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final PopupReviewRepository popupReviewRepository;
    private final PopupPolicy popupPolicy;

    // 팝업 등록
    @Transactional
    public PopupDto.CreatePopupRes createPopup(CustomUserDetails user, PopupDto.CreatePopupReq req, List<String> urls) throws BaseException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REGISTER_FAIL_UNAUTHORIZED)
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

        return PopupDto.CreatePopupRes.builder().popupIdx(popup.getIdx()).build();

    }

    // 팝업 조회
    public PopupDto.SearchPopupRes getPopup(Long popupIdx) throws BaseException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_SEARCH_FAIL_NOT_FOUND)
        );

        return popup.toDto();

    }

    // 팝업 목록 조회
    public Page<PopupDto.SearchPopupRes> getPopups(String status, String keyword, int page, int size) throws BaseException {

        // 팝업 페이지 조회(keyword, status, pageable) 조회
        // true : 검색어가 있는 경우, 상태에 따라 활성화된 또는 종료된 팝업을 검색어로 페이징 조회
        // false : 검색어가 없는 경우, 상태에 따라 활성화 또는 종료된 팝업을 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Popup> popupPage = (keyword != null)
                ? popupRepository.findAllByKeywordAndStatus(keyword, status, pageable)
                : popupRepository.findAllByStatus(status, pageable);

        // DTO 반환 (데이터가 없어도 빈 페이지 반환)
        return Popup.toDtoPage(popupPage);

    }

    // 팝업 목록 조회(기업용)
    public Page<PopupDto.SearchPopupRes> getMyPopups(CustomUserDetails user, String keyword, int page, int size) throws BaseException {

        // 팝업 페이지 조회(keyword, email, pageable) 조회
        // true : 키워드(keyword)가 있는 경우, 등록된 기업회원의 이메일과 키워드로 페이징 조회
        // false : 키워드(keyword)가 없는 경우, 등록된 기업회원의 이메일로 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Popup> popupPage = (keyword != null)
            ? popupRepository.findAllByKeywordAndCompanyEmail(keyword, user.getEmail(), pageable)
            : popupRepository.findAllByCompanyEmail(user.getEmail(), pageable);

        return Popup.toDtoPage(popupPage);

    }

    // 팝업 수정
    @Transactional
    public PopupDto.UpdatePopupRes updatePopup(CustomUserDetails user, Long popupIdx, PopupDto.UpdatePopupReq req, List<String> urls) throws BaseException {

        // 팝업 조회(popupIdx, email)
        Popup popup = popupRepository.findByPopupIdxAndCompanyEmail(popupIdx, user.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_UPDATE_FAIL_NOT_FOUND)
        );

        // 팝업 수정
        popup.update(req);

        // 팝업 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지 저장
        if (urls != null) {
            popup.replaceImages(urls);
        }

        // DTO 반환
        return PopupDto.UpdatePopupRes.builder().popupIdx(popup.getIdx()).build();

    }

    // 팝업 삭제
    @Transactional
    public void deletePopup(CustomUserDetails user, Long popupIdx) throws BaseException{

        // 팝업 조회(popupIdx, email)
        Popup popup = popupRepository.findByPopupIdxAndCompanyEmail(popupIdx, user.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );

        // 팝업 소프트 삭제
        popup.endPopup();

    }

    // 팝업 좋아요 증감
    @Transactional
    public void togglePopupLike(CustomUserDetails user, Long popupIdx) throws BaseException {

        // 팝업 인덱스로 조회 없으면 예외 반환
        Popup popup = popupRepository.findByPopupIdx(popupIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_LIKE_FAIL_NOT_FOUND)
        );

        // 좋아요 증감
        // if : 이미 좋아요를 누른 상태면 좋아요 삭제 / 팝업 좋아요 개수 감소(직접 쿼리 활용)
        // else : 좋아요를 처음 누르면 좋아요 저장 / 팝업 좋아요 개수 증가(직접 쿼리 활용)
        Optional<PopupLike> popupLike = popupLikeRepository.findByCustomerIdxAndPopupIdx(user.getIdx(), popupIdx);
        if (popupLike.isPresent()) {
            popupRepository.incrementLikeCount(popupIdx);
            popup.decreaseLike();
        } else {
            popupLikeRepository.save(PopupLike.create(popup, user.getIdx()));
            popup.increaseLike();
        }

    }

    // 팝업 좋아요 목록 조회(고객용)
    public Page<PopupDto.SearchPopupLikeRes> getMyLikedPopups(CustomUserDetails user, int page, int size) throws BaseException {

        // 팝업 페이지 조회(customerIdx)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<PopupLike> popupLikePage = popupLikeRepository.findAllByCustomerIdx(user.getIdx(), pageable);

        return PopupLike.toDtoPage(popupLikePage);

    }

    // 팝업 리뷰 등록
    @Transactional
    public PopupDto.CreatePopupReviewRes createPopupReview(CustomUserDetails user, Long popupIdx, PopupDto.CreatePopupReviewReq req) throws BaseException {

        // 결제 조회(popupIdx, customerIdx, 결제 완료 상태) / 결제한 사람만 리뷰 작성 가능
        ordersDetailRepository.existsReviewableOrder(user.getIdx(), popupIdx, List.of(OrdersStatus.STOCK_COMPLETE, OrdersStatus.RESERVE_COMPLETE)).orElseThrow(() ->
                new BaseException(BaseMessage.STORE_REVIEW_FAIL_INVALID_MEMBER)
        );

        // 팝업 조회 (popupIdx)
        Popup popup = popupRepository.findById(popupIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REVIEW_FAIL_NOT_FOUND)
        );

        // 팝업 리뷰 조회(popupIdx, customerIdx) / 팝업 하나당 한개의 리뷰 작성 가능
        Optional<PopupReview> popupReviewOpt = popupReviewRepository.findByPopupIdxAndCustomerIdx(popupIdx, user.getIdx());
        if(popupReviewOpt.isPresent()) {
            throw new BaseException(BaseMessage.STORE_REVIEW_FAIL_DUPLICATED);
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

        return PopupDto.CreatePopupReviewRes.builder().reviewIdx(popupReview.getIdx()).build();

    }

    // 팝업 리뷰 목록 조회
    public Page<PopupDto.SearchPopupReviewRes> getPopupReviews(Long popupIdx, int page, int size) throws BaseException {

        // 리뷰 조회(popupIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<PopupReview> popupReviewPage = popupReviewRepository.findAllByPopupIdx(popupIdx, pageable);
        return PopupReview.toDtoPage(popupReviewPage);

    }

    // 팝업 리뷰 목록 조회(고객용)
    public Page<PopupDto.SearchPopupReviewRes> getMyReviews(CustomUserDetails user, int page, int size) throws BaseException {

        // 리뷰 목록 조회(customerIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<PopupReview> popupReviewPage = popupReviewRepository.findAllByCustomerIdx(user.getIdx(), pageable);

        return PopupReview.toDtoPage(popupReviewPage);

    }

    // 기업 정산 금액 조회
    public Page<PayoutDto.SearchPayoutRes> getPopupPayouts(CustomUserDetails user, Long popupIdx, int page, int size) throws BaseException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findByPopupIdx(popupIdx)
                .orElseThrow(() -> new BaseException(BaseMessage.PAYOUT_SEARCH_FAIL_NOT_FOUND_STORE));

        popupPolicy.validateOwner(popup, user);

        // 팝업 페이지 조회(popupIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Payout> payoutPage = payoutRepository.findAllByPopupIdx(popupIdx, pageable);

        return Payout.toDtoPage(payoutPage);

    }

}
