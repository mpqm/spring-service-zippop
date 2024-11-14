package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import com.fiiiiive.zippop.store.model.dto.SearchStoreLikeRes;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.entity.StoreLike;
import com.fiiiiive.zippop.store.repository.StoreImageRepository;
import com.fiiiiive.zippop.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreLikeService {
    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final CustomerRepository customerRepository;

    // 팝업 스토어 좋아요 증감
    @Transactional
    public void like(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        // 예외: 팝업 스토어가 존재하지 않을때, 사용자가 존재하지 않을때, 기업 회원일때
        if(Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_LIKE_FAIL_INVALID_ROLE);
        }
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

    public List<SearchStoreLikeRes> searchAll(CustomUserDetails customUserDetails) throws BaseException {
        List<StoreLike> storeLikeList = storeLikeRepository.findAllByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(()->new BaseException(BaseResponseMessage.FAVORITE_SEARCH_ALL_FAIL));
        List<SearchStoreLikeRes> searchStoreLikeResList = new ArrayList<>();
        for(StoreLike storeLike: storeLikeList){
            Store store = storeLike.getStore();
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
            for(StoreImage storeImage : store.getStoreImageList()){
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getIdx())
                        .storeImageUrl(storeImage.getUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            SearchStoreLikeRes searchStoreLikeRes = SearchStoreLikeRes.builder()
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
                    .createdAt(store.getCreatedAt())
                    .updatedAt(store.getUpdatedAt())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
            searchStoreLikeResList.add(searchStoreLikeRes);
        }
        return searchStoreLikeResList;
    }
}
