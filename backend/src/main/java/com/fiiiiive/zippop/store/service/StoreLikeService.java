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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
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

    public Page<SearchStoreLikeRes> searchAll(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        Page<StoreLike> result = storeLikeRepository.findAllByCustomerIdx(customUserDetails.getIdx(), PageRequest.of(page, size))
        .orElseThrow(()->new BaseException(BaseResponseMessage.FAVORITE_SEARCH_ALL_FAIL));
        Page<SearchStoreLikeRes> searchStoreLikeResPage = result.map(storeLike -> {
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
            for(StoreImage storeImage : storeLike.getStore().getStoreImageList()){
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getIdx())
                        .storeImageUrl(storeImage.getUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            return SearchStoreLikeRes.builder()
                    .storeIdx(storeLike.getStore().getIdx())
                    .companyEmail(storeLike.getStore().getCompanyEmail())
                    .storeName(storeLike.getStore().getName())
                    .storeContent(storeLike.getStore().getContent())
                    .storeAddress(storeLike.getStore().getAddress())
                    .category(storeLike.getStore().getCategory())
                    .likeCount(storeLike.getStore().getLikeCount())
                    .totalPeople(storeLike.getStore().getTotalPeople())
                    .storeStartDate(storeLike.getStore().getStartDate())
                    .storeEndDate(storeLike.getStore().getEndDate())
                    .createdAt(storeLike.getCreatedAt())
                    .updatedAt(storeLike.getUpdatedAt())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        return searchStoreLikeResPage;
    }
}
