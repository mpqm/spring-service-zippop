package com.fiiiiive.zippop.settlement.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.SearchOrdersRes;
import com.fiiiiive.zippop.settlement.model.dto.SearchSettlementRes;
import com.fiiiiive.zippop.settlement.model.entity.Settlement;
import com.fiiiiive.zippop.settlement.repository.SettlementRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {
    private final StoreRepository storeRepository;
    private final SettlementRepository settlementRepository;

    // 기업 정산 금액 조회
    public Page<SearchSettlementRes> searchSettlement(CustomUserDetails customUserDetails, Long storeIdx, int page, int size) throws BaseException {
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.SETTLEMENT_SEARCH_FAIL_NOT_FOUND_STORE)
        );
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.SETTLEMENT_SEARCH_FAIL_INVALID_MEMBER);
        }
        Page<Settlement> settlementPage = settlementRepository.findAllByStoreIdx(storeIdx, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).orElseThrow(
                () -> new BaseException(BaseResponseMessage.SETTLEMENT_SEARCH_FAIL_NOT_FOUND)
        );

        // 2. Orders DTO Page 반환
        Page<SearchSettlementRes> searchSettlementResPage = settlementPage.map(settlement -> {
            return SearchSettlementRes.builder()
                    .settlementDate(settlement.getSettlementDate())
                    .totalRevenue(settlement.getTotalRevenue())
                    .build();
        });
        return searchSettlementResPage;
    }
}
