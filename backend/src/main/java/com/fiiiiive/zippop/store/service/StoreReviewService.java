package com.fiiiiive.zippop.store.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreReviewService {
    private final StoreReviewRepository storeReviewRepository;
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    
    // 리뷰 등록
    @Transactional
    public CreateStoreReviewRes register(CustomUserDetails customUserDetails, Long storeIdx, CreateStoreReviewReq dto) throws BaseException {
        // 예외처리: 고객 사용자가 존재하지 않을때, 팝업스토어가 존재하지 않을때
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_INVALID_MEMBER));
        Store store = storeRepository.findById(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Optional<StoreReview> resultStoreReview = storeReviewRepository.findByStoreIdxAndCustomerIdx(storeIdx, customUserDetails.getIdx());
        if(resultStoreReview.isPresent()){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_DUPLICATED);
        }
        StoreReview storeReview = StoreReview.builder()
                .customerEmail(customUserDetails.getEmail())
                .customerName(customer.getName())
                .title(dto.getReviewTitle())
                .content(dto.getReviewContent())
                .rating(dto.getReviewRating())
                .store(store)
                .customer(customer)
                .build();
        storeReviewRepository.save(storeReview);
        return CreateStoreReviewRes.builder().reviewIdx(storeReview.getIdx()).build();
    }

    // 팝업 스토어에 등록된 리뷰들 조회
    public Page<SearchStoreReviewRes> searchAllAsGuest(Long storeIdx, int page, int size) throws BaseException {
        Page<StoreReview> result = storeReviewRepository.findByStoreIdx(storeIdx, PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Page<SearchStoreReviewRes> getReviewResPage = result.map(review -> SearchStoreReviewRes.builder()
                .reviewIdx(review.getIdx())
                .customerName(review.getCustomerName())
                .customerEmail(review.getCustomerEmail())
                .reviewTitle(review.getTitle())
                .reviewContent(review.getContent())
                .reviewRating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build());
        return getReviewResPage;
    }

    // 고객이 작성한 팝업 스토어 리뷰 목록 조회
    public Page<SearchStoreReviewRes> searchAllAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        Page<StoreReview> result = storeReviewRepository.findAllByCustomerIdx(customUserDetails.getIdx(), PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Page<SearchStoreReviewRes> getReviewResPage = result.map(review -> SearchStoreReviewRes.builder()
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
        return getReviewResPage;
    }
}