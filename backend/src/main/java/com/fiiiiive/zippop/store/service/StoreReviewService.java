package com.fiiiiive.zippop.store.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.store.model.entity.StoreReview;
import com.fiiiiive.zippop.store.model.dto.CreateStoreReviewReq;
import com.fiiiiive.zippop.store.model.dto.CreateStoreReviewRes;
import com.fiiiiive.zippop.store.model.dto.SearchStoreReviewRes;
import com.fiiiiive.zippop.store.repository.StoreReviewRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreReviewService {

    private final StoreReviewRepository storeReviewRepository;
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    private final OrdersRepository ordersRepository;

    // 팝업 스토어 리뷰 등록
    @Transactional
    public CreateStoreReviewRes register(CustomUserDetails customUserDetails, Long storeIdx, CreateStoreReviewReq dto) throws BaseException {
        // 1. 예외처리: 고객 사용자가 존재하지 않을 때, 팝업스토어가 존재하지 않을때, 기업회원 일 때, 결제를 완료하지 않은 사용자 일 때
        if(Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")){
            throw new BaseException(BaseResponseMessage.STORE_REVIEW_FAIL_INVALID_ROLE);
        }
        Orders orders = ordersRepository.findByStoreIdxAndCustomerIdxAndStatus(storeIdx, customUserDetails.getIdx(), "_COMPLETE").orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_LIKE_FAIL_INVALID_MEMBER)
        );
        Store store = storeRepository.findById(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_REVIEW_FAIL_NOT_FOUND)
        );
        Optional<StoreReview> resultStoreReview = storeReviewRepository.findByStoreIdxAndCustomerIdx(storeIdx, customUserDetails.getIdx());
        if(resultStoreReview.isPresent()){
            throw new BaseException(BaseResponseMessage.STORE_REVIEW_FAIL_DUPLICATED);
        }

        // 2. StoreReview 저장 및 반환
        StoreReview storeReview = StoreReview.builder()
                .customerEmail(customUserDetails.getEmail())
                .customerName(orders.getCustomer().getName())
                .title(dto.getReviewTitle())
                .content(dto.getReviewContent())
                .rating(dto.getReviewRating())
                .store(store)
                .customer(orders.getCustomer())
                .build();
        storeReviewRepository.save(storeReview);
        return CreateStoreReviewRes.builder().reviewIdx(storeReview.getIdx()).build();
    }

    // 팝업 스토어 리뷰 목록 조회(전체)
    public Page<SearchStoreReviewRes> searchAll(Long storeIdx, int page, int size) throws BaseException {
        // 1. 예외 : 리뷰 목록 조회 결과가 없을 때
        Page<StoreReview> storeReviewPage = storeReviewRepository.findAllByStoreIdx(storeIdx, PageRequest.of(page, size)).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_REVIEW_SEARCH_ALL_FAIL_NOT_FOUND)
        );
        // 2. StoreReview DTO Page 반환
        Page<SearchStoreReviewRes> searchStoreReviewResPage = storeReviewPage.map(review -> SearchStoreReviewRes.builder()
                .reviewIdx(review.getIdx())
                .customerName(review.getCustomerName())
                .customerEmail(review.getCustomerEmail())
                .reviewTitle(review.getTitle())
                .reviewContent(review.getContent())
                .reviewRating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build());
        return searchStoreReviewResPage;
    }

    // 팝업 스토어 리뷰 목록 조회(고객)
    public Page<SearchStoreReviewRes> searchAllAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        // 1. 예외 : 리뷰 목록 조회 결과가 없을 때
        Page<StoreReview> storeReviewPage = storeReviewRepository.findAllByCustomerIdx(customUserDetails.getIdx(), PageRequest.of(page, size)).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_REVIEW_SEARCH_ALL_FAIL_NOT_FOUND)
        );
        // 2. StoreReview DTO Page 반환
        Page<SearchStoreReviewRes> searchStoreReviewResPage = storeReviewPage.map(review -> SearchStoreReviewRes.builder()
                .reviewIdx(review.getIdx())
                .storeName(review.getStore().getName())
                .customerEmail(review.getCustomerEmail())
                .customerName(review.getCustomerName())
                .reviewTitle(review.getTitle())
                .reviewContent(review.getContent())
                .reviewRating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build());
        return searchStoreReviewResPage;
    }
}