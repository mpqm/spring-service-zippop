package com.fiiiiive.zippop.review.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.review.model.entity.Review;
import com.fiiiiive.zippop.review.model.entity.ReviewImage;
import com.fiiiiive.zippop.review.model.dto.CreateReviewReq;
import com.fiiiiive.zippop.review.model.dto.CreateReviewRes;
import com.fiiiiive.zippop.review.model.dto.GetReviewImageRes;
import com.fiiiiive.zippop.review.model.dto.GetReviewRes;
import com.fiiiiive.zippop.review.repository.ReviewImageRepository;
import com.fiiiiive.zippop.review.repository.ReviewRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final CustomerRepository customerRepository;

    public CreateReviewRes register(CustomUserDetails customUserDetails, Long storeIdx, List<String> fileNames, CreateReviewReq dto) throws BaseException {
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_INVALID_MEMBER));
        Store store = storeRepository.findById(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Review review = Review.builder()
                .customerEmail(customUserDetails.getEmail())
                .reviewTitle(dto.getReviewTitle())
                .reviewContent(dto.getReviewContent())
                .rating(dto.getRating())
                .store(store)
                .customer(customer)
                .build();
        reviewRepository.save(review);
        List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
        for(String fileName: fileNames) {
            ReviewImage reviewImage = ReviewImage.builder()
                    .imageUrl(fileName)
                    .review(review)
                    .build();
            reviewImageRepository.save(reviewImage);
            GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                    .reviewImageIdx(reviewImage.getReviewImageIdx())
                    .imageUrl(reviewImage.getImageUrl())
                    .createdAt(reviewImage.getCreatedAt())
                    .updatedAt(reviewImage.getUpdatedAt())
                    .build();
            getReviewImageResList.add(getReviewImageRes);
        }
        return CreateReviewRes.builder()
                .reviewIdx(review.getReviewIdx())
                .customerEmail(review.getCustomerEmail())
                .reviewTitle(review.getReviewTitle())
                .reviewContent(review.getReviewContent())
                .rating(review.getRating())
                .getReviewImageResList(getReviewImageResList)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public Page<GetReviewRes> searchStore(Long storeIdx, int page, int size) throws BaseException {
        Page<Review> result = reviewRepository.findByStoreIdx(storeIdx, PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Page<GetReviewRes> getPopupReviewResPage = result.map(popupReview -> {
            List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
            List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
            for(ReviewImage popupReviewImage : reviewImageList){
                GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                    .reviewImageIdx(popupReviewImage.getReviewImageIdx())
                    .imageUrl(popupReviewImage.getImageUrl())
                    .createdAt(popupReviewImage.getCreatedAt())
                    .updatedAt(popupReviewImage.getUpdatedAt())
                    .build();
                getReviewImageResList.add(getReviewImageRes);
            }
            GetReviewRes getReviewRes = GetReviewRes.builder()
                    .reviewIdx(popupReview.getReviewIdx())
                    .customerEmail(popupReview.getCustomerEmail())
                    .reviewTitle(popupReview.getReviewTitle())
                    .reviewContent(popupReview.getReviewContent())
                    .rating(popupReview.getRating())
                    .createdAt(popupReview.getCreatedAt())
                    .updatedAt(popupReview.getUpdatedAt())
                    .getReviewImageResList(getReviewImageResList)
                    .build();
            return getReviewRes;
        });
        return getPopupReviewResPage;
    }

    public Page<GetReviewRes> searchCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        Page<Review> result = reviewRepository.findByCustomerEmail(customUserDetails.getEmail(), PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REVIEW_FAIL_STORE_NOT_EXIST));
        Page<GetReviewRes> getPopupReviewResPage = result.map(popupReview -> {
            List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
            List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
            for(ReviewImage popupReviewImage : reviewImageList){
                GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                        .reviewImageIdx(popupReviewImage.getReviewImageIdx())
                        .imageUrl(popupReviewImage.getImageUrl())
                        .createdAt(popupReviewImage.getCreatedAt())
                        .updatedAt(popupReviewImage.getUpdatedAt())
                        .build();
                getReviewImageResList.add(getReviewImageRes);
            }
            GetReviewRes getReviewRes = GetReviewRes.builder()
                    .reviewIdx(popupReview.getReviewIdx())
                    .customerEmail(popupReview.getCustomerEmail())
                    .reviewTitle(popupReview.getReviewTitle())
                    .reviewContent(popupReview.getReviewContent())
                    .rating(popupReview.getRating())
                    .createdAt(popupReview.getCreatedAt())
                    .updatedAt(popupReview.getUpdatedAt())
                    .getReviewImageResList(getReviewImageResList)
                    .build();
            return getReviewRes;
        });
        return getPopupReviewResPage;
    }
}