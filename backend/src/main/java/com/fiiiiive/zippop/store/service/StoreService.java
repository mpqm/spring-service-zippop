package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.common.exception.BaseException;
import com.fiiiiive.zippop.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import com.fiiiiive.zippop.member.repository.CompanyRepository;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.member.model.entity.Company;
import com.fiiiiive.zippop.member.model.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.goods.model.entity.PopupGoods;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsImageRes;
import com.fiiiiive.zippop.review.model.dto.GetReviewImageRes;
import com.fiiiiive.zippop.review.model.entity.Review;
import com.fiiiiive.zippop.review.model.entity.ReviewImage;
import com.fiiiiive.zippop.review.model.dto.GetReviewRes;
import com.fiiiiive.zippop.store.model.dto.*;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.entity.StoreLike;
import com.fiiiiive.zippop.store.model.dto.GetStoreRes;
import com.fiiiiive.zippop.store.repository.StoreImageRepository;
import com.fiiiiive.zippop.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final CompanyRepository companyRepository;
    private final StoreImageRepository storeImageRepository;
    private final CustomerRepository customerRepository;
    private final StoreLikeRepository storeLikeRepository;

    public CreateStoreRes register(CustomUserDetails customUserDetails, CreateStoreReq dto, List<String> fileNames) throws BaseException {
        Company company = companyRepository.findByCompanyEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REGISTER_FAIL_UNAUTHORIZED));
        Store store = Store.builder()
                .companyEmail(customUserDetails.getEmail())
                .name(dto.getStoreName())
                .content(dto.getStoreContent())
                .storeAddress(dto.getStoreAddress())
                .category(dto.getCategory())
                .storeStartDate(dto.getStoreStartDate())
                .storeEndDate(dto.getStoreEndDate())
                .likeCount(0)
                .company(company)
                .build();
        storeRepository.save(store);
        List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
        for(String fileName : fileNames){
            StoreImage storeImage = StoreImage.builder()
                    .imageUrl(fileName)
                    .store(store)
                    .build();
            storeImageRepository.save(storeImage);
            GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                    .storeImageIdx(storeImage.getStoreImageIdx())
                    .imageUrl(storeImage.getImageUrl())
                    .createdAt(storeImage.getCreatedAt())
                    .updatedAt(storeImage.getUpdatedAt())
                    .build();
            getStoreImageResList.add(getStoreImageRes);
        }
        return CreateStoreRes.builder()
                .storeIdx(store.getStoreIdx())
                .companyEmail(store.getCompanyEmail())
                .storeName(store.getStoreName())
                .storeContent(store.getStoreContent())
                .storeAddress(store.getStoreAddress())
                .category(store.getCategory())
                .likeCount(store.getLikeCount())
                .totalPeople(store.getTotalPeople())
                .storeStartDate(store.getStoreStartDate())
                .storeEndDate(store.getStoreEndDate())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .getStoreImageResList(getStoreImageResList)
                .build();
    }

    public GetStoreRes search(Long storeIdx) throws BaseException {
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
        List<StoreImage> storeImageList = store.getPopupstoreImageList();
        for (StoreImage storeImage : storeImageList) {
            GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                    .storeImageIdx(storeImage.getStoreImageIdx())
                    .imageUrl(storeImage.getImageUrl())
                    .createdAt(storeImage.getCreatedAt())
                    .updatedAt(storeImage.getUpdatedAt())
                    .build();
            getStoreImageResList.add(getStoreImageRes);
        }
        List<GetGoodsRes> getGoodsResList = new ArrayList<>();
        List<PopupGoods> popupGoodsList = store.getPopupGoodsList();
        for (PopupGoods popupGoods : popupGoodsList) {
            List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
            List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
            for(GoodsImage goodsImage : goodsImageList){
                GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                        .productImageIdx(goodsImage.getPopupGoodsImageIdx())
                        .imageUrl(goodsImage.getImageUrl())
                        .createdAt(goodsImage.getCreatedAt())
                        .updatedAt(goodsImage.getUpdatedAt())
                        .build();
                getGoodsImageResList.add(getGoodsImageRes);
            }
            GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                    .productIdx(popupGoods.getProductIdx())
                    .productName(popupGoods.getProductName())
                    .productPrice(popupGoods.getProductPrice())
                    .productContent(popupGoods.getProductContent())
                    .productAmount(popupGoods.getProductAmount())
                    .createdAt(popupGoods.getCreatedAt())
                    .updatedAt(popupGoods.getUpdatedAt())
                    .getGoodsImageResList(getGoodsImageResList)
                    .build();
            getGoodsResList.add(getGoodsRes);
        }
        List<GetReviewRes> getReviewResList = new ArrayList<>();
        List<Review> reviewList = store.getReviewList();
        for (Review review : reviewList) {
            List<ReviewImage> reviewImageList = review.getReviewImageList();
            List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
            for(ReviewImage reviewImage : reviewImageList){
                GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                        .reviewImageIdx(reviewImage.getReviewImageIdx())
                        .imageUrl(reviewImage.getImageUrl())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .build();
                getReviewImageResList.add(getReviewImageRes);
            }
            GetReviewRes getReviewRes = GetReviewRes.builder()
                    .reviewIdx(review.getReviewIdx())
                    .reviewTitle(review.getReviewTitle())
                    .reviewContent(review.getReviewContent())
                    .rating(review.getRating())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .getReviewImageResList(getReviewImageResList)
                    .build();
            getReviewResList.add(getReviewRes);
        }
        GetStoreRes getStoreRes = GetStoreRes.builder()
                .storeIdx(store.getStoreIdx())
                .companyEmail(store.getCompanyEmail())
                .storeName(store.getStoreName())
                .storeContent(store.getStoreContent())
                .storeAddress(store.getStoreAddress())
                .category(store.getCategory())
                .likeCount(store.getLikeCount())
                .totalPeople(store.getTotalPeople())
                .storeStartDate(store.getStoreStartDate())
                .storeEndDate(store.getStoreEndDate())
                .getGoodsResList(getGoodsResList)
                .getReviewResList(getReviewResList)
                .getStoreImageResList(getStoreImageResList)
                .build();
            return getStoreRes;
    }

    public Page<GetStoreRes> searchAll(int page, int size) throws BaseException {
        Page<Store> result = storeRepository.findAll(PageRequest.of(page, size));
        if (!result.hasContent()) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST);
        }
        Page<GetStoreRes> getPopupStoreResList = result.map(popupStore -> {
            List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
            List<StoreImage> storeImageList = popupStore.getPopupstoreImageList();
            for (StoreImage popupStoreImage : storeImageList) {
                GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                        .storeImageIdx(popupStoreImage.getStoreImageIdx())
                        .imageUrl(popupStoreImage.getImageUrl())
                        .createdAt(popupStoreImage.getCreatedAt())
                        .updatedAt(popupStoreImage.getUpdatedAt())
                        .build();
                getStoreImageResList.add(getStoreImageRes);
            }
            GetStoreRes getStoreRes = GetStoreRes.builder()
                    .storeIdx(popupStore.getStoreIdx())
                    .companyEmail(popupStore.getCompanyEmail())
                    .storeName(popupStore.getStoreName())
                    .storeContent(popupStore.getStoreContent())
                    .storeAddress(popupStore.getStoreAddress())
                    .category(popupStore.getCategory())
                    .likeCount(popupStore.getLikeCount())
                    .totalPeople(popupStore.getTotalPeople())
                    .storeStartDate(popupStore.getStoreStartDate())
                    .storeEndDate(popupStore.getStoreEndDate())
                    .getStoreImageResList(getStoreImageResList)
                    .build();
            return getStoreRes;
        });
        return getPopupStoreResList;
    }

    public Page<GetStoreRes> searchCompany(CustomUserDetails customUserDetails, int page, int size) throws BaseException{
        Page<Store> result = storeRepository.findByCompanyEmail(customUserDetails.getEmail(), PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        Page<GetStoreRes> getPopupStoreResList = result.map(popupStore -> {
            List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
            List<StoreImage> storeImageList = popupStore.getPopupstoreImageList();
            for (StoreImage popupStoreImage : storeImageList) {
                GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                        .storeImageIdx(popupStoreImage.getStoreImageIdx())
                        .imageUrl(popupStoreImage.getImageUrl())
                        .createdAt(popupStoreImage.getCreatedAt())
                        .updatedAt(popupStoreImage.getUpdatedAt())
                        .build();
                getStoreImageResList.add(getStoreImageRes);
            }
            GetStoreRes getStoreRes = GetStoreRes.builder()
                    .storeIdx(popupStore.getStoreIdx())
                    .companyEmail(popupStore.getCompanyEmail())
                    .storeName(popupStore.getStoreName())
                    .storeContent(popupStore.getStoreContent())
                    .storeAddress(popupStore.getStoreAddress())
                    .category(popupStore.getCategory())
                    .likeCount(popupStore.getLikeCount())
                    .totalPeople(popupStore.getTotalPeople())
                    .storeStartDate(popupStore.getStoreStartDate())
                    .storeEndDate(popupStore.getStoreEndDate())
                    .getStoreImageResList(getStoreImageResList)
                    .build();
            return getStoreRes;
        });
        return getPopupStoreResList;
    }

    public Page<GetStoreRes> searchKeyword(String keyword, int page, int size) throws BaseException{
        Page<Store> result = storeRepository.findByKeyword(keyword, PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        Page<GetStoreRes> getPopupStoreResList = result.map(popupStore -> {
            List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
            List<StoreImage> storeImageList = popupStore.getPopupstoreImageList();
            for (StoreImage popupStoreImage : storeImageList) {
                GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                        .storeImageIdx(popupStoreImage.getStoreImageIdx())
                        .imageUrl(popupStoreImage.getImageUrl())
                        .createdAt(popupStoreImage.getCreatedAt())
                        .updatedAt(popupStoreImage.getUpdatedAt())
                        .build();
                getStoreImageResList.add(getStoreImageRes);
            }
            List<GetGoodsRes> getGoodsResList = new ArrayList<>();
            List<PopupGoods> popupGoodsList = popupStore.getPopupGoodsList();
            for (PopupGoods popupGoods : popupGoodsList) {
                List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
                List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
                for(GoodsImage popupGoodsImage: goodsImageList){
                    GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                            .productImageIdx(popupGoodsImage.getPopupGoodsImageIdx())
                            .imageUrl(popupGoodsImage.getImageUrl())
                            .createdAt(popupGoodsImage.getCreatedAt())
                            .updatedAt(popupGoodsImage.getUpdatedAt())
                            .build();
                    getGoodsImageResList.add(getGoodsImageRes);
                }
                GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                        .productIdx(popupGoods.getProductIdx())
                        .productName(popupGoods.getProductName())
                        .productPrice(popupGoods.getProductPrice())
                        .productContent(popupGoods.getProductContent())
                        .productAmount(popupGoods.getProductAmount())
                        .createdAt(popupGoods.getCreatedAt())
                        .updatedAt(popupGoods.getUpdatedAt())
                        .getGoodsImageResList(getGoodsImageResList)
                        .build();
                getGoodsResList.add(getGoodsRes);
            }
            List<GetReviewRes> getReviewResList = new ArrayList<>();
            List<Review> reviewList = popupStore.getReviewList();
            for (Review popupReview : reviewList) {
                List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
                List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
                for(ReviewImage popupReviewImage : reviewImageList){
                    GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                            .reviewImageIdx(popupReviewImage.getReviewImageIdx())
                            .imageUrl(popupReviewImage.getImageUrl())
                            .createdAt(popupReview.getCreatedAt())
                            .updatedAt(popupReview.getUpdatedAt())
                            .build();
                    getReviewImageResList.add(getReviewImageRes);
                }
                GetReviewRes getReviewRes = GetReviewRes.builder()
                        .reviewIdx(popupReview.getReviewIdx())
                        .reviewTitle(popupReview.getReviewTitle())
                        .reviewContent(popupReview.getReviewContent())
                        .rating(popupReview.getRating())
                        .createdAt(popupReview.getCreatedAt())
                        .updatedAt(popupReview.getUpdatedAt())
                        .getReviewImageResList(getReviewImageResList)
                        .build();
                getReviewResList.add(getReviewRes);
            }
            GetStoreRes getStoreRes = GetStoreRes.builder()
                    .storeIdx(popupStore.getStoreIdx())
                    .companyEmail(popupStore.getCompanyEmail())
                    .storeName(popupStore.getStoreName())
                    .storeContent(popupStore.getStoreContent())
                    .storeAddress(popupStore.getStoreAddress())
                    .category(popupStore.getCategory())
                    .likeCount(popupStore.getLikeCount())
                    .totalPeople(popupStore.getTotalPeople())
                    .storeStartDate(popupStore.getStoreStartDate())
                    .storeEndDate(popupStore.getStoreEndDate())
                    .getGoodsResList(getGoodsResList)
                    .getReviewResList(getReviewResList)
                    .getStoreImageResList(getStoreImageResList)
                    .build();
            return getStoreRes;
        });
        return getPopupStoreResList;
    }

    public Page<GetStoreRes> searchDateRange(LocalDateTime storeStartDate, LocalDateTime storeEndDate, int page, int size) throws BaseException{
        Page<Store> result = storeRepository.findByStoreDateRange(storeStartDate, storeEndDate, PageRequest.of(page, size))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        Page<GetStoreRes> getPopupStoreResList = result.map(popupStore -> {
            List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
            List<StoreImage> storeImageList = popupStore.getPopupstoreImageList();
            for (StoreImage popupStoreImage : storeImageList) {
                GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                        .storeImageIdx(popupStoreImage.getStoreImageIdx())
                        .imageUrl(popupStoreImage.getImageUrl())
                        .createdAt(popupStoreImage.getCreatedAt())
                        .updatedAt(popupStoreImage.getUpdatedAt())
                        .build();
                getStoreImageResList.add(getStoreImageRes);
            }
            List<GetGoodsRes> getGoodsResList = new ArrayList<>();
            List<PopupGoods> popupGoodsList = popupStore.getPopupGoodsList();
            for (PopupGoods popupGoods : popupGoodsList) {
                List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
                List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
                for(GoodsImage popupGoodsImage: goodsImageList){
                    GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                            .productImageIdx(popupGoodsImage.getPopupGoodsImageIdx())
                            .imageUrl(popupGoodsImage.getImageUrl())
                            .createdAt(popupGoodsImage.getCreatedAt())
                            .updatedAt(popupGoodsImage.getUpdatedAt())
                            .build();
                    getGoodsImageResList.add(getGoodsImageRes);
                }
                GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                        .productIdx(popupGoods.getProductIdx())
                        .productName(popupGoods.getProductName())
                        .productPrice(popupGoods.getProductPrice())
                        .productContent(popupGoods.getProductContent())
                        .productAmount(popupGoods.getProductAmount())
                        .createdAt(popupGoods.getCreatedAt())
                        .updatedAt(popupGoods.getUpdatedAt())
                        .getGoodsImageResList(getGoodsImageResList)
                        .build();
                getGoodsResList.add(getGoodsRes);
            }
            List<GetReviewRes> getReviewResList = new ArrayList<>();
            List<Review> reviewList = popupStore.getReviewList();
            for (Review popupReview : reviewList) {
                List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
                List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
                for(ReviewImage popupReviewImage : reviewImageList){
                    GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                            .reviewImageIdx(popupReviewImage.getReviewImageIdx())
                            .imageUrl(popupReviewImage.getImageUrl())
                            .createdAt(popupReview.getCreatedAt())
                            .updatedAt(popupReview.getUpdatedAt())
                            .build();
                    getReviewImageResList.add(getReviewImageRes);
                }
                GetReviewRes getReviewRes = GetReviewRes.builder()
                        .reviewIdx(popupReview.getReviewIdx())
                        .reviewTitle(popupReview.getReviewTitle())
                        .reviewContent(popupReview.getReviewContent())
                        .rating(popupReview.getRating())
                        .createdAt(popupReview.getCreatedAt())
                        .updatedAt(popupReview.getUpdatedAt())
                        .getReviewImageResList(getReviewImageResList)
                        .build();
                getReviewResList.add(getReviewRes);
            }
            GetStoreRes getStoreRes = GetStoreRes.builder()
                    .storeIdx(popupStore.getStoreIdx())
                    .companyEmail(popupStore.getCompanyEmail())
                    .storeName(popupStore.getStoreName())
                    .storeContent(popupStore.getStoreContent())
                    .storeAddress(popupStore.getStoreAddress())
                    .category(popupStore.getCategory())
                    .likeCount(popupStore.getLikeCount())
                    .totalPeople(popupStore.getTotalPeople())
                    .storeStartDate(popupStore.getStoreStartDate())
                    .storeEndDate(popupStore.getStoreEndDate())
                    .getGoodsResList(getGoodsResList)
                    .getReviewResList(getReviewResList)
                    .getStoreImageResList(getStoreImageResList)
                    .build();
            return getStoreRes;
        });
        return getPopupStoreResList;
    }

    public UpdateStoreRes update(CustomUserDetails customUserDetails, Long storeIdx, UpdateStoreReq dto, List<String> fileNames) throws BaseException {
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        store.setStoreName(dto.getStoreName());
        store.setStoreContent(dto.getStoreContent());
        store.setStoreAddress(dto.getStoreAddress());
        store.setCategory(dto.getCategory());
        store.setTotalPeople(dto.getTotalPeople());
        store.setStoreEndDate(dto.getStoreEndDate());
        store.setStoreEndDate(dto.getStoreEndDate());
        storeRepository.save(store);
        List<StoreImage> storeImageList = store.getPopupstoreImageList();
        List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
        for(StoreImage storeImage : storeImageList){
            storeImageRepository.deleteById(storeImage.getStoreImageIdx());
        }
        for (String fileName: fileNames) {
            StoreImage popupstoreImage = StoreImage.builder()
                    .imageUrl(fileName)
                    .store(store)
                    .build();
            storeImageRepository.save(popupstoreImage);
            GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                    .storeImageIdx(popupstoreImage.getStoreImageIdx())
                    .imageUrl(popupstoreImage.getImageUrl())
                    .createdAt(popupstoreImage.getCreatedAt())
                    .updatedAt(popupstoreImage.getUpdatedAt())
                    .build();
            getStoreImageResList.add(getStoreImageRes);
        }
        return UpdateStoreRes.builder()
                .storeIdx(store.getStoreIdx())
                .companyEmail(store.getCompanyEmail())
                .storeName(store.getStoreName())
                .storeContent(store.getStoreContent())
                .storeAddress(store.getStoreAddress())
                .category(store.getCategory())
                .likeCount(store.getLikeCount())
                .totalPeople(store.getTotalPeople())
                .storeStartDate(store.getStoreStartDate())
                .storeEndDate(store.getStoreEndDate())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .getStoreImageResList(getStoreImageResList)
                .build();
    }

    public void delete(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException{
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_NOT_FOUND));
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_INVALID_MEMBER);
        }
        storeRepository.deleteById(storeIdx);
    }

    @Transactional
    public void like(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException{
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_LIKE_FAIL_NOT_FOUND));
        Customer customer = customerRepository.findById(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_LIKE_FAIL_INVALID_MEMBER));
        Optional<StoreLike> result = storeLikeRepository.findByCustomerEmailAndStoreIdx(customUserDetails.getEmail(),storeIdx);
        if (result.isEmpty()) {
            store.setLikeCount(store.getLikeCount() + 1);
            storeRepository.save(store);
            StoreLike storeLike = StoreLike.builder()
                    .store(store)
                    .customer(customer)
                    .build();
            storeLikeRepository.save(storeLike);
        } else {
            store.setLikeCount(store.getLikeCount() - 1);
            storeRepository.save(store);
            storeLikeRepository.deleteByCustomerEmailAndStoreIdx(customer.getEmail(), storeIdx);
        }
    }

    public Page<GetStoreRes> searchCategory(String category, int page, int size) throws BaseException {
        Page<Store> result = storeRepository.findByCategory(category, PageRequest.of(page, size))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        Page<GetStoreRes> getPopupStoreResList = result.map(popupStore -> {
            List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
            List<StoreImage> storeImageList = popupStore.getPopupstoreImageList();
            for (StoreImage popupStoreImage : storeImageList) {
                GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                        .storeImageIdx(popupStoreImage.getStoreImageIdx())
                        .imageUrl(popupStoreImage.getImageUrl())
                        .createdAt(popupStoreImage.getCreatedAt())
                        .updatedAt(popupStoreImage.getUpdatedAt())
                        .build();
                getStoreImageResList.add(getStoreImageRes);
            }
            List<GetGoodsRes> getGoodsResList = new ArrayList<>();
            List<PopupGoods> popupGoodsList = popupStore.getPopupGoodsList();
            for (PopupGoods popupGoods : popupGoodsList) {
                List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
                List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
                for(GoodsImage popupGoodsImage: goodsImageList){
                    GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                            .productImageIdx(popupGoodsImage.getPopupGoodsImageIdx())
                            .imageUrl(popupGoodsImage.getImageUrl())
                            .createdAt(popupGoodsImage.getCreatedAt())
                            .updatedAt(popupGoodsImage.getUpdatedAt())
                            .build();
                    getGoodsImageResList.add(getGoodsImageRes);
                }
                GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                        .productIdx(popupGoods.getProductIdx())
                        .productName(popupGoods.getProductName())
                        .productPrice(popupGoods.getProductPrice())
                        .productContent(popupGoods.getProductContent())
                        .productAmount(popupGoods.getProductAmount())
                        .createdAt(popupGoods.getCreatedAt())
                        .updatedAt(popupGoods.getUpdatedAt())
                        .getGoodsImageResList(getGoodsImageResList)
                        .build();
                getGoodsResList.add(getGoodsRes);
            }
            List<GetReviewRes> getReviewResList = new ArrayList<>();
            List<Review> reviewList = popupStore.getReviewList();
            for (Review popupReview : reviewList) {
                List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
                List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
                for(ReviewImage popupReviewImage : reviewImageList){
                    GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                            .reviewImageIdx(popupReviewImage.getReviewImageIdx())
                            .imageUrl(popupReviewImage.getImageUrl())
                            .createdAt(popupReview.getCreatedAt())
                            .updatedAt(popupReview.getUpdatedAt())
                            .build();
                    getReviewImageResList.add(getReviewImageRes);
                }
                GetReviewRes getReviewRes = GetReviewRes.builder()
                        .reviewIdx(popupReview.getReviewIdx())
                        .reviewTitle(popupReview.getReviewTitle())
                        .reviewContent(popupReview.getReviewContent())
                        .rating(popupReview.getRating())
                        .createdAt(popupReview.getCreatedAt())
                        .updatedAt(popupReview.getUpdatedAt())
                        .getReviewImageResList(getReviewImageResList)
                        .build();
                getReviewResList.add(getReviewRes);
            }
            GetStoreRes getStoreRes = GetStoreRes.builder()
                    .storeIdx(popupStore.getStoreIdx())
                    .companyEmail(popupStore.getCompanyEmail())
                    .storeName(popupStore.getStoreName())
                    .storeContent(popupStore.getStoreContent())
                    .storeAddress(popupStore.getStoreAddress())
                    .category(popupStore.getCategory())
                    .likeCount(popupStore.getLikeCount())
                    .totalPeople(popupStore.getTotalPeople())
                    .storeStartDate(popupStore.getStoreStartDate())
                    .storeEndDate(popupStore.getStoreEndDate())
                    .getGoodsResList(getGoodsResList)
                    .getReviewResList(getReviewResList)
                    .getStoreImageResList(getStoreImageResList)
                    .build();
            return getStoreRes;
        });
        return getPopupStoreResList;
    }

    public GetStoreRes searchStore(String storeName) throws BaseException{
        Store store = storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
        List<StoreImage> storeImageList = store.getPopupstoreImageList();
        for (StoreImage storeImage : storeImageList) {
            GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                    .storeImageIdx(storeImage.getStoreImageIdx())
                    .imageUrl(storeImage.getImageUrl())
                    .createdAt(storeImage.getCreatedAt())
                    .updatedAt(storeImage.getUpdatedAt())
                    .build();
            getStoreImageResList.add(getStoreImageRes);
        }
        List<GetGoodsRes> getGoodsResList = new ArrayList<>();
        List<PopupGoods> popupGoodsList = store.getPopupGoodsList();
        for (PopupGoods popupGoods : popupGoodsList) {
            List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
            List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
            for(GoodsImage goodsImage : goodsImageList){
                GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                        .productImageIdx(goodsImage.getPopupGoodsImageIdx())
                        .imageUrl(goodsImage.getImageUrl())
                        .createdAt(goodsImage.getCreatedAt())
                        .updatedAt(goodsImage.getUpdatedAt())
                        .build();
                getGoodsImageResList.add(getGoodsImageRes);
            }
            GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                    .productIdx(popupGoods.getProductIdx())
                    .productName(popupGoods.getProductName())
                    .productPrice(popupGoods.getProductPrice())
                    .productContent(popupGoods.getProductContent())
                    .productAmount(popupGoods.getProductAmount())
                    .createdAt(popupGoods.getCreatedAt())
                    .updatedAt(popupGoods.getUpdatedAt())
                    .getGoodsImageResList(getGoodsImageResList)
                    .build();
            getGoodsResList.add(getGoodsRes);
        }
        List<GetReviewRes> getReviewResList = new ArrayList<>();
        List<Review> reviewList = store.getReviewList();
        for (Review review : reviewList) {
            List<ReviewImage> reviewImageList = review.getReviewImageList();
            List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
            for(ReviewImage reviewImage : reviewImageList){
                GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                        .reviewImageIdx(reviewImage.getReviewImageIdx())
                        .imageUrl(reviewImage.getImageUrl())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .build();
                getReviewImageResList.add(getReviewImageRes);
            }
            GetReviewRes getReviewRes = GetReviewRes.builder()
                    .reviewIdx(review.getReviewIdx())
                    .reviewTitle(review.getReviewTitle())
                    .reviewContent(review.getReviewContent())
                    .rating(review.getRating())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .getReviewImageResList(getReviewImageResList)
                    .build();
            getReviewResList.add(getReviewRes);
        }
        GetStoreRes getStoreRes = GetStoreRes.builder()
                .storeIdx(store.getStoreIdx())
                .companyEmail(store.getCompanyEmail())
                .storeName(store.getStoreName())
                .storeContent(store.getStoreContent())
                .storeAddress(store.getStoreAddress())
                .category(store.getCategory())
                .likeCount(store.getLikeCount())
                .totalPeople(store.getTotalPeople())
                .storeStartDate(store.getStoreStartDate())
                .storeEndDate(store.getStoreEndDate())
                .getGoodsResList(getGoodsResList)
                .getReviewResList(getReviewResList)
                .getStoreImageResList(getStoreImageResList)
                .build();
        return getStoreRes;
    }

    public Page<GetStoreRes> searchAddress(String storeAddress, int page, int size) throws BaseException{
        Page<Store> result = storeRepository.findByStoreAddress(storeAddress, PageRequest.of(page, size));
        if (!result.hasContent()) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST);
        }
        Page<GetStoreRes> getPopupStoreResPage = result.map(popupStore -> {
            List<GetStoreImageRes> getStoreImageResList = new ArrayList<>();
            List<StoreImage> storeImageList = popupStore.getPopupstoreImageList();
            for (StoreImage popupStoreImage : storeImageList) {
                GetStoreImageRes getStoreImageRes = GetStoreImageRes.builder()
                        .storeImageIdx(popupStoreImage.getStoreImageIdx())
                        .imageUrl(popupStoreImage.getImageUrl())
                        .createdAt(popupStoreImage.getCreatedAt())
                        .updatedAt(popupStoreImage.getUpdatedAt())
                        .build();
                getStoreImageResList.add(getStoreImageRes);
            }
            List<GetGoodsRes> getGoodsResList = new ArrayList<>();
            List<PopupGoods> popupGoodsList = popupStore.getPopupGoodsList();
            for (PopupGoods popupGoods : popupGoodsList) {
                List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
                List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
                for(GoodsImage popupGoodsImage: goodsImageList){
                    GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                            .productImageIdx(popupGoodsImage.getPopupGoodsImageIdx())
                            .imageUrl(popupGoodsImage.getImageUrl())
                            .createdAt(popupGoodsImage.getCreatedAt())
                            .updatedAt(popupGoodsImage.getUpdatedAt())
                            .build();
                    getGoodsImageResList.add(getGoodsImageRes);
                }
                GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                        .productIdx(popupGoods.getProductIdx())
                        .productName(popupGoods.getProductName())
                        .productPrice(popupGoods.getProductPrice())
                        .productContent(popupGoods.getProductContent())
                        .productAmount(popupGoods.getProductAmount())
                        .createdAt(popupGoods.getCreatedAt())
                        .updatedAt(popupGoods.getUpdatedAt())
                        .getGoodsImageResList(getGoodsImageResList)
                        .build();
                getGoodsResList.add(getGoodsRes);
            }
            List<GetReviewRes> getReviewResList = new ArrayList<>();
            List<Review> reviewList = popupStore.getReviewList();
            for (Review popupReview : reviewList) {
                List<ReviewImage> reviewImageList = popupReview.getReviewImageList();
                List<GetReviewImageRes> getReviewImageResList = new ArrayList<>();
                for(ReviewImage popupReviewImage : reviewImageList){
                    GetReviewImageRes getReviewImageRes = GetReviewImageRes.builder()
                            .reviewImageIdx(popupReviewImage.getReviewImageIdx())
                            .imageUrl(popupReviewImage.getImageUrl())
                            .createdAt(popupReview.getCreatedAt())
                            .updatedAt(popupReview.getUpdatedAt())
                            .build();
                    getReviewImageResList.add(getReviewImageRes);
                }
                GetReviewRes getReviewRes = GetReviewRes.builder()
                        .reviewIdx(popupReview.getReviewIdx())
                        .reviewTitle(popupReview.getReviewTitle())
                        .reviewContent(popupReview.getReviewContent())
                        .rating(popupReview.getRating())
                        .createdAt(popupReview.getCreatedAt())
                        .updatedAt(popupReview.getUpdatedAt())
                        .getReviewImageResList(getReviewImageResList)
                        .build();
                getReviewResList.add(getReviewRes);
            }
            GetStoreRes getStoreRes = GetStoreRes.builder()
                    .storeIdx(popupStore.getStoreIdx())
                    .companyEmail(popupStore.getCompanyEmail())
                    .storeName(popupStore.getStoreName())
                    .storeContent(popupStore.getStoreContent())
                    .storeAddress(popupStore.getStoreAddress())
                    .category(popupStore.getCategory())
                    .likeCount(popupStore.getLikeCount())
                    .totalPeople(popupStore.getTotalPeople())
                    .storeStartDate(popupStore.getStoreStartDate())
                    .storeEndDate(popupStore.getStoreEndDate())
                    .getGoodsResList(getGoodsResList)
                    .getReviewResList(getReviewResList)
                    .getStoreImageResList(getStoreImageResList)
                    .build();
            return getStoreRes;
        });
        return getPopupStoreResPage;
    }
}