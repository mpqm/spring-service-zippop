package com.fiiiiive.zippop.review.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.review.model.dto.SearchReviewImageRes;
import com.fiiiiive.zippop.review.model.entity.Review;
import com.fiiiiive.zippop.review.model.entity.ReviewImage;
import com.fiiiiive.zippop.review.model.dto.CreateReviewReq;
import com.fiiiiive.zippop.review.model.dto.CreateReviewRes;
import com.fiiiiive.zippop.review.model.dto.SearchReviewRes;
import com.fiiiiive.zippop.review.repository.ReviewImageRepository;
import com.fiiiiive.zippop.review.repository.ReviewRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final CustomerRepository customerRepository;
    
    // 리뷰 등록
    @Transactional
    public CreateReviewRes register(CustomUserDetails customUserDetails, Long storeIdx, List<String> fileNames, CreateReviewReq dto) throws BaseException {
        // 예외처리: 고객 사용자가 존재하지 않을때, 팝업스토어가 존재하지 않을때
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_INVALID_MEMBER));
        Store store = storeRepository.findById(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Review review = Review.builder()
                .customerEmail(customUserDetails.getEmail())
                .title(dto.getReviewTitle())
                .content(dto.getReviewContent())
                .rating(dto.getReviewRating())
                .store(store)
                .customer(customer)
                .build();
        reviewRepository.save(review);
        for(String fileName: fileNames) {
            ReviewImage reviewImage = ReviewImage.builder()
                    .url(fileName)
                    .review(review)
                    .build();
            reviewImageRepository.save(reviewImage);
        }
        return CreateReviewRes.builder().reviewIdx(review.getIdx()).build();
    }

    // 팝업 스토어에 등록된 리뷰들 조회
    public Page<SearchReviewRes> searchAllAsGuest(Long storeIdx, int page, int size) throws BaseException {
        Page<Review> result = reviewRepository.findByStoreIdx(storeIdx, PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Page<SearchReviewRes> getPopupReviewResPage = result.map(popupReview -> {
            List<SearchReviewImageRes> searchReviewImageResList = new ArrayList<>();
            List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
            for(ReviewImage popupReviewImage : reviewImageList){
                SearchReviewImageRes searchReviewImageRes = SearchReviewImageRes.builder()
                    .reviewImageIdx(popupReviewImage.getIdx())
                    .imageUrl(popupReviewImage.getUrl())
                    .createdAt(popupReviewImage.getCreatedAt())
                    .updatedAt(popupReviewImage.getUpdatedAt())
                    .build();
                searchReviewImageResList.add(searchReviewImageRes);
            }
            return SearchReviewRes.builder()
                    .reviewIdx(popupReview.getIdx())
                    .customerEmail(popupReview.getCustomerEmail())
                    .reviewTitle(popupReview.getTitle())
                    .reviewContent(popupReview.getContent())
                    .reviewRating(popupReview.getRating())
                    .createdAt(popupReview.getCreatedAt())
                    .updatedAt(popupReview.getUpdatedAt())
                    .searchReviewImageResList(searchReviewImageResList)
                    .build();
        });
        return getPopupReviewResPage;
    }
    // 고객이 작성한 팝업 스토어 리뷰 목록 조회
    public Page<SearchReviewRes> searchAllAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        Page<Review> result = reviewRepository.findByCustomerIdx(customUserDetails.getIdx(), PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Page<SearchReviewRes> getPopupReviewResPage = result.map(popupReview -> {
            List<SearchReviewImageRes> searchReviewImageResList = new ArrayList<>();
            List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
            for(ReviewImage popupReviewImage : reviewImageList){
                SearchReviewImageRes searchReviewImageRes = SearchReviewImageRes.builder()
                        .reviewImageIdx(popupReviewImage.getIdx())
                        .imageUrl(popupReviewImage.getUrl())
                        .createdAt(popupReviewImage.getCreatedAt())
                        .updatedAt(popupReviewImage.getUpdatedAt())
                        .build();
                searchReviewImageResList.add(searchReviewImageRes);
            }
            return SearchReviewRes.builder()
                    .reviewIdx(popupReview.getIdx())
                    .customerEmail(popupReview.getCustomerEmail())
                    .reviewTitle(popupReview.getTitle())
                    .reviewContent(popupReview.getContent())
                    .reviewRating(popupReview.getRating())
                    .createdAt(popupReview.getCreatedAt())
                    .updatedAt(popupReview.getUpdatedAt())
                    .searchReviewImageResList(searchReviewImageResList)
                    .build();
        });
        return getPopupReviewResPage;
    }
}