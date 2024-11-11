package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsImageRes;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import com.fiiiiive.zippop.review.model.dto.SearchReviewImageRes;
import com.fiiiiive.zippop.review.model.entity.Review;
import com.fiiiiive.zippop.review.model.entity.ReviewImage;
import com.fiiiiive.zippop.review.model.dto.SearchReviewRes;
import com.fiiiiive.zippop.store.model.dto.*;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.model.entity.StoreLike;
import com.fiiiiive.zippop.store.repository.StoreImageRepository;
import com.fiiiiive.zippop.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final CompanyRepository companyRepository;
    private final StoreImageRepository storeImageRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final CustomerRepository customerRepository;

    // 팝업 스토어 등록
    @Transactional
    public CreateStoreRes register(CustomUserDetails customUserDetails, CreateStoreReq dto, List<String> fileNameList) throws BaseException {
        // 예외: 팝업 스토어 등록은  기업 회원만 가능
        Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_REGISTER_FAIL_UNAUTHORIZED));

        // Store 엔티티 생성 및 저장
        Store store = Store.builder()
                .companyEmail(customUserDetails.getEmail())
                .name(dto.getStoreName())
                .content(dto.getStoreContent())
                .address(dto.getStoreAddress())
                .category(dto.getCategory())
                .totalPeople(dto.getTotalPeople())
                .startDate(dto.getStoreStartDate())
                .endDate(dto.getStoreEndDate())
                .likeCount(0)
                .company(company)
                .build();
        storeRepository.save(store);

        // Store Image 엔티티들 생성 및 저장
        for(String fileName : fileNameList){
            StoreImage storeImage = StoreImage.builder()
                    .url(fileName)
                    .store(store)
                    .build();
            storeImageRepository.save(storeImage);
        }

        // CreateStoreRes 반환
        return CreateStoreRes.builder().storeIdx(store.getIdx()).build();
    }

    // 팝업 스토어 단일 조회 (idx, name)
    public SearchStoreRes search(Long storeIdx, String storeName) throws BaseException {
        // idx, name / 예외: 팝업 스토어가 존재하지 않을때
        Store store = null;
        if(storeIdx != null && storeName == null){
            store = storeRepository.findByStoreIdx(storeIdx)
            .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        } else if(storeIdx == null && storeName != null) {
            store = storeRepository.findByStoreName(storeName)
            .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        } else {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_INVALID_REQUEST);
        }

        // Store Image Dto 생성
        List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
        for (StoreImage storeImage : store.getStoreImageList()) {
            SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                    .storeImageIdx(storeImage.getIdx())
                    .storeImageUrl(storeImage.getUrl())
                    .createdAt(storeImage.getCreatedAt())
                    .updatedAt(storeImage.getUpdatedAt())
                    .build();
            searchStoreImageResList.add(searchStoreImageRes);
        }

        // Goods Dto List 생성
        List<SearchGoodsRes> searchGoodsResList = new ArrayList<>();
        for (Goods goods : store.getGoodsList()) {
            // Goods Image Dto 생성
            List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();
            for(GoodsImage goodsImage : goods.getGoodsImageList()){
                SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                        .goodsImageIdx(goodsImage.getIdx())
                        .imageUrl(goodsImage.getUrl())
                        .createdAt(goodsImage.getCreatedAt())
                        .updatedAt(goodsImage.getUpdatedAt())
                        .build();
                searchGoodsImageResList.add(searchGoodsImageRes);
            }
            // Goods Dto 생성
            SearchGoodsRes searchGoodsRes = SearchGoodsRes.builder()
                    .goodsIdx(goods.getIdx())
                    .goodsName(goods.getName())
                    .goodsPrice(goods.getPrice())
                    .goodsContent(goods.getContent())
                    .goodsAmount(goods.getAmount())
                    .createdAt(goods.getCreatedAt())
                    .updatedAt(goods.getUpdatedAt())
                    .searchGoodsImageResList(searchGoodsImageResList)
                    .build();
            searchGoodsResList.add(searchGoodsRes);
        }

        // Review Dto List 생성
        List<SearchReviewRes> searchReviewResList = new ArrayList<>();
        for (Review review :  store.getReviewList()) {
            // Review Image Dto 생성
            List<SearchReviewImageRes> searchReviewImageResList = new ArrayList<>();
            for(ReviewImage reviewImage : review.getReviewImageList()){
                SearchReviewImageRes searchReviewImageRes = SearchReviewImageRes.builder()
                        .reviewImageIdx(reviewImage.getIdx())
                        .imageUrl(reviewImage.getUrl())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .build();
                searchReviewImageResList.add(searchReviewImageRes);
            }
            // Review Dto 생성
            SearchReviewRes searchReviewRes = SearchReviewRes.builder()
                    .reviewIdx(review.getIdx())
                    .reviewTitle(review.getTitle())
                    .reviewContent(review.getContent())
                    .reviewRating(review.getRating())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .searchReviewImageResList(searchReviewImageResList)
                    .build();
            searchReviewResList.add(searchReviewRes);
        }

        // GetStoreRes 반환
        return SearchStoreRes.builder()
                .storeIdx(store.getIdx())
                .companyEmail(store.getCompanyEmail())
                .storeName(store.getName())
                .storeContent(store.getContent())
                .storeAddress(store.getAddress())
                .category(store.getCategory())
                .likeCount(store.getLikeCount())
                .totalPeople(store.getTotalPeople())
                .storeStartDate(store.getStartDate())
                .storeEndDate(store.getEndDate())
                .searchGoodsResList(searchGoodsResList)
                .searchReviewResList(searchReviewResList)
                .searchStoreImageResList(searchStoreImageResList)
                .build();
    }
    
    // 기업 사용자가 등록한 팝업 스토어 조회
    public Page<SearchStoreRes> searchAllAsCompany(CustomUserDetails customUserDetails, String keyword, int page, int size) throws BaseException {
        // 예외: 기업 사용자 이메일로 조회된 팝업 스토어가 없을때,
        Page<Store> result = null;
        if(keyword != null) {
            result = storeRepository.findByKeywordAndCompanyEmail(customUserDetails.getEmail(), keyword, PageRequest.of(page, size));
        } else {
            result = storeRepository.findByCompanyEmail(customUserDetails.getEmail(), PageRequest.of(page, size));
        }
        // 예외: 값이 없다면
        if (!result.hasContent()) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST);
        }
        Page<SearchStoreRes> searchStoreResPage = result.map(store -> {
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
            for (StoreImage storeImage : store.getStoreImageList()) {
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getIdx())
                        .storeImageUrl(storeImage.getUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            return SearchStoreRes.builder()
                    .storeIdx(store.getIdx())
                    .companyEmail(store.getCompanyEmail())
                    .storeName(store.getName())
                    .storeContent(store.getContent())
                    .storeAddress(store.getAddress())
                    .category(store.getCategory())
                    .likeCount(store.getLikeCount())
                    .totalPeople(store.getTotalPeople())
                    .storeStartDate(store.getStartDate())
                    .storeEndDate(store.getEndDate())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        return searchStoreResPage;
    }
    
    // 팝업 스토어 목록 조회 (page)
    public Page<SearchStoreRes> searchAllAsGuest(String keyword, int page, int size) throws BaseException {

        Page<Store> result = null;
        if(keyword != null) {
            result = storeRepository.findByKeyword(keyword, PageRequest.of(page, size));
        } else {
            result = storeRepository.findAll(PageRequest.of(page, size));
        }

        // 예외: 값이 없다면
        if (!result.hasContent()) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST);
        }

        // 페이징 적용
        Page<SearchStoreRes> searchStoreResPage = result.map(store -> {
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();

            // Store Image Dto List 생성
            for (StoreImage storeImage : store.getStoreImageList()) {
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getIdx())
                        .storeImageUrl(storeImage.getUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            
            // GetStoreRes 반환
            return SearchStoreRes.builder()
                    .storeIdx(store.getIdx())
                    .companyEmail(store.getCompanyEmail())
                    .storeName(store.getName())
                    .storeContent(store.getContent())
                    .storeAddress(store.getAddress())
                    .category(store.getCategory())
                    .likeCount(store.getLikeCount())
                    .totalPeople(store.getTotalPeople())
                    .storeStartDate(store.getStartDate())
                    .storeEndDate(store.getEndDate())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        // GetStoreResList 반환
        return searchStoreResPage;
    }

    // 팝업 스토어 좋아요 증감
    @Transactional
    public void like(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        // 예외: 팝업 스토어가 존재하지 않을때, 사용자가 존재하지 않을때,
        Store store = storeRepository.findByStoreIdx(storeIdx)
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_LIKE_FAIL_NOT_FOUND));
        Customer customer = customerRepository.findById(customUserDetails.getIdx())
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_LIKE_FAIL_INVALID_MEMBER));
        Optional<StoreLike> storeLikeOpt = storeLikeRepository.findByCustomerIdxAndStoreIdx(customer.getIdx(),storeIdx);
        if (storeLikeOpt.isPresent()) {
            // 이미 좋아요를 누른 상태면 좋아요 취소
            storeLikeRepository.deleteByCustomerIdxAndStoreIdx(customer.getIdx(), storeIdx);
            storeRepository.decrementLikeCount(storeIdx); // 좋아요 개수 감소 (직접 쿼리 활용)
        } else {
            // 좋아요 추가
            StoreLike storeLike = StoreLike.builder()
                    .store(store)
                    .customer(customer)
                    .build();
            storeLikeRepository.save(storeLike);
            storeRepository.incrementLikeCount(storeIdx); // 좋아요 개수 증가 (직접 쿼리 활용)
        }
    }

    // 팝업 스토어 수정
    @Transactional
    public UpdateStoreRes update(CustomUserDetails customUserDetails, Long storeIdx, UpdateStoreReq dto, List<String> fileNames) throws BaseException {
        // 예외: 팝업 스토어가 존재하지 않을때, 현재 사용자가 이메일이 다를때
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        // Store 업데이트
        store.setName(dto.getStoreName());
        store.setContent(dto.getStoreContent());
        store.setAddress(dto.getStoreAddress());
        store.setCategory(dto.getCategory());
        store.setTotalPeople(dto.getTotalPeople());
        store.setEndDate(dto.getStoreEndDate());
        store.setStartDate(dto.getStoreStartDate());
        storeRepository.save(store);
        
        // Store Image 업데이트
        // 기존 이미지 URL 목록 불러오기
        List<String> existingUrls = new ArrayList<>();
        for (StoreImage storeImage : store.getStoreImageList()) {
            existingUrls.add(storeImage.getUrl());
        }

        // 삭제할 이미지 URL (기존에 있었으나 새 목록에 없는 것들)
        for (StoreImage storeImage : store.getStoreImageList()) {
            if (!fileNames.contains(storeImage.getUrl())) {
                storeImageRepository.deleteById(storeImage.getIdx());
            }
        }

        // 추가할 이미지 URL (새 목록에 있는데 기존 목록에는 없는 것들)
        for (String fileName : fileNames) {
            if (!existingUrls.contains(fileName)) {
                StoreImage storeImage = StoreImage.builder()
                        .url(fileName)
                        .store(store)
                        .build();
                storeImageRepository.save(storeImage);
            }
        }

        // UpdateStoreRes 반환
        return UpdateStoreRes.builder().storeIdx(store.getIdx()).build();
    }

    // 팝업 스토어 삭제
    @Transactional
    public void delete(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException{
        // 예외: 팝업 스토어가 존재하지 않을때, 현재 사용자가 이메일이 다를때
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_NOT_FOUND));
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_INVALID_MEMBER);
        }
        storeRepository.deleteById(storeIdx);
    }

}