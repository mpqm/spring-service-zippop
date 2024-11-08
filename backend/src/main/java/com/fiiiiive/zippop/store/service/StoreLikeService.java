package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreLike;
import com.fiiiiive.zippop.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreLikeService {
    private final CustomerRepository customerRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final StoreRepository storeRepository;

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
}
