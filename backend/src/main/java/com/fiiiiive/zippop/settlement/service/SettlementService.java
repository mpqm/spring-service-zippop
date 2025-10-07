package com.fiiiiive.zippop.settlement.service;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.settlement.dto.SettlementDto;
import com.fiiiiive.zippop.settlement.entity.Settlement;
import com.fiiiiive.zippop.settlement.repository.SettlementRepository;
import com.fiiiiive.zippop.store.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<SettlementDto.SearchSettlementRes> searchSettlement(CustomUserDetails customUserDetails, Long storeIdx, int page, int size) throws BaseException {

        // 스토어 조회(storeIdx)
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.SETTLEMENT_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 소유 조회
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) throw new BaseException(BaseMessage.SETTLEMENT_SEARCH_FAIL_INVALID_MEMBER);

        // 스토어 페이지 조회(storeIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Settlement> settlementPage = settlementRepository.findAllByStoreIdx(storeIdx, pageable).orElseThrow(
                () -> new BaseException(BaseMessage.SETTLEMENT_SEARCH_FAIL_NOT_FOUND)
        );

        // DTO 반환
        return Settlement.toDtoPage(settlementPage);

    }

}
