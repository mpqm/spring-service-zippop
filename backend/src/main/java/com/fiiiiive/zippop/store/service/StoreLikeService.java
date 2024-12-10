package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import com.fiiiiive.zippop.store.model.dto.SearchStoreLikeRes;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.entity.StoreLike;
import com.fiiiiive.zippop.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreLikeService {

    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final CustomerRepository customerRepository;

    /* 팝업 스토어 좋아요 증감 */
    @Transactional
    public void like(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        // 팝업 스토어 인덱스로 조회 없으면 예외 반환
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_LIKE_FAIL_NOT_FOUND)
        );
        // 고객 회원 인덱스로 조회 없으면 예외 반환
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_LIKE_FAIL_INVALID_MEMBER)
        );

        // 좋아요 증감
        // if : 이미 좋아요를 누른 상태면 좋아요 삭제 / 스토어 좋아요 개수 감소(직접 쿼리 활용)
        // else : 좋아요를 처음 누르면 좋아요 저장 / 스토어 좋아요 개수 증가(직접 쿼리 활용)
        Optional<StoreLike> storeLikeOpt = storeLikeRepository.findByCustomerIdxAndStoreIdx(customer.getIdx(),storeIdx);
        if (storeLikeOpt.isPresent()) {
            storeLikeRepository.deleteByCustomerIdxAndStoreIdx(customer.getIdx(), storeIdx);
            storeRepository.decrementLikeCount(storeIdx);
        } else {
            StoreLike storeLike = StoreLike.builder()
                    .store(store)
                    .customer(customer)
                    .build();
            storeLikeRepository.save(storeLike);
            storeRepository.incrementLikeCount(storeIdx);
        }
    }

    /* 팝업 스토어 좋아요 목록 (고객 회원) 조회 */
    public Page<SearchStoreLikeRes> searchAll(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        // 팝업 스토어 목록 조회 없으면 예외 반환
        Page<StoreLike> storeLikePage = storeLikeRepository.findAllByCustomerIdx(customUserDetails.getIdx(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_LIKE_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // StoreLike Page DTO  반환
        return storeLikePage.map(storeLike -> {
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
    }
}
