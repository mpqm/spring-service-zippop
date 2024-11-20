package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.store.model.dto.*;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.repository.StoreImageRepository;
import com.fiiiiive.zippop.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
                .status("STORE_START")
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
                .storeStatus(store.getStatus())
                .storeStartDate(store.getStartDate())
                .storeEndDate(store.getEndDate())
                .searchStoreImageResList(searchStoreImageResList)
                .build();
    }
    
    // 기업 사용자가 등록한 팝업 스토어 조회
    public Page<SearchStoreRes> searchAllAsCompany(CustomUserDetails customUserDetails, String keyword, int page, int size) throws BaseException {
        // 예외: 기업 사용자 이메일로 조회된 팝업 스토어가 없을때,
        Page<Store> result = null;
        if(keyword != null) {
            result = storeRepository.findByKeywordAndCompanyEmail(keyword, customUserDetails.getEmail(), PageRequest.of(page, size));
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
                    .storeStatus(store.getStatus())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        return searchStoreResPage;
    }

    // 팝업 스토어 목록 조회 (page) flag: 1은 종료되지않은 0은 종료됨
    public Page<SearchStoreRes> searchAllAsGuest(Boolean flag, String keyword, int page, int size) throws BaseException {
        Page<Store> result;
        String storeStart = "STORE_START";
        String storeEnd = "STORE_END";
        if(keyword != null) {
            if(flag){
                result = storeRepository.findAllByKeywordAndStatus( keyword, storeStart, PageRequest.of(page, size));
            } else {
                result = storeRepository.findAllByKeywordAndStatus( keyword, storeEnd, PageRequest.of(page, size));
            }
        } else {
            if(flag){
                result = storeRepository.findAllByStatus("STORE_START",PageRequest.of(page, size));
            } else {
                result = storeRepository.findAllByStatus("STORE_END", PageRequest.of(page, size));
            }
        }

        // 예외: 값이 없다면
        if (result.isEmpty()) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST);
        }

        // 페이징 적용 수수료 결제가 되지 않은 팝업 스토어는 메인에 표시하지않는다.
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
                    .storeStatus(store.getStatus())
                    .storeEndDate(store.getEndDate())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        // GetStoreResList 반환
        return searchStoreResPage;
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
        if(fileNames != null){
            storeImageRepository.deleteAllByStoreIdx(storeIdx);
            for (String fileName : fileNames) {
                StoreImage storeImage = StoreImage.builder()
                        .url(fileName)
                        .store(store)
                        .build();
                storeImageRepository.save(storeImage);
            }
        }
        // 추가할 이미지 URL (새 목록에 있는데 기존 목록에는 없는 것들)


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