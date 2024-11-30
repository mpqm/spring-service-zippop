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

    // 팝업 스토어 등록
    @Transactional
    public CreateStoreRes register(CustomUserDetails customUserDetails, CreateStoreReq dto, List<String> fileNameList) throws BaseException {
        // 1. 예외: 기업회원을 찾을 수 없을 때
        Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_REGISTER_FAIL_UNAUTHORIZED)
        );

        // 2. Store 및 StoreImage 엔티티 생성 및 저장
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
        for(String fileName : fileNameList){
            StoreImage storeImage = StoreImage.builder()
                    .url(fileName)
                    .store(store)
                    .build();
            storeImageRepository.save(storeImage);
        }
        return CreateStoreRes.builder().storeIdx(store.getIdx()).build();
    }

    // 팝업 스토어 단일 조회 (idx, name)
    public SearchStoreRes search(Long storeIdx) throws BaseException {
        // 1. 예외: 팝업 스토어가 존재하지 않을때
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_SEARCH_FAIL_NOT_FOUND)
        );

        // 2. Store Dto 반환
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
                .storeStatus(store.getStatus())
                .storeStartDate(store.getStartDate())
                .storeEndDate(store.getEndDate())
                .searchStoreImageResList(searchStoreImageResList)
                .build();
    }

    // 팝업 스토어 목록 전체 회원 조회
    public Page<SearchStoreRes> searchAll(Boolean flag, String keyword, int page, int size) throws BaseException {
        /*
        1. 팝업 스토어 목록 조회 flag가 ture면 팝업스토어가 활성화된 상태 false면 종료된 상태
        if : 키워드가 있을때
            if : flag가 true일 때 팝업스토어가 활성화된 상태와 키워드로 페이징 조회
            else : flag가 false일 때 팝업스토어가 종료된 상태와 키워드로 페이징 조회
        else : 키워드가 없을때
            if : flag가 true일 때 팝업스토어가 활성화된 상태를 페이징 조회
            else : flag가 false일 때 팝업스토어가 종료된 상태를 페이징 조회
        예외: 팝업 스토어 조회 결과가 없을 때
         */
        Page<Store> storePage;
        if(keyword != null) {
            if(flag){
                storePage = storeRepository.findAllByKeywordAndStatus( keyword, "STORE_START", PageRequest.of(page, size));
            } else {
                storePage = storeRepository.findAllByKeywordAndStatus( keyword, "STORE_END", PageRequest.of(page, size));
            }
        } else {
            if(flag){
                storePage = storeRepository.findAllByStatus("STORE_START",PageRequest.of(page, size));
            } else {
                storePage = storeRepository.findAllByStatus("STORE_END", PageRequest.of(page, size));
            }
        }
        if (storePage.isEmpty()) {
            throw new BaseException(BaseResponseMessage.STORE_SEARCH_ALL_FAIL_NOT_FOUND);
        }

        // 2. Store DTO Page 반환
        Page<SearchStoreRes> searchStoreResPage = storePage.map(store -> {
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
                    .storeStatus(store.getStatus())
                    .storeEndDate(store.getEndDate())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
        });
        return searchStoreResPage;
    }

    // 팝업 스토어 목록 기업 회원 조회
    public Page<SearchStoreRes> searchAllAsCompany(CustomUserDetails customUserDetails, String keyword, int page, int size) throws BaseException {
        /*
        1. 기업 회원이 등록한 팝업 스토어 페이징 조회
        if : 검색어와 인증된 사용자의 이메일 주소가 같은 팝업 스토어를 페이징 조회
        else : 검색어가 없으면 인등된 사용자의 이메일 주소가 같은 팝업 스토어를 페이징 조회
        예외: 기업 사용자 이메일로 조회된 팝업 스토어가 없을때
        */
        Page<Store> storePage = null;
        if(keyword != null) {
            storePage = storeRepository.findAllByKeywordAndCompanyEmail(keyword, customUserDetails.getEmail(), PageRequest.of(page, size));
        } else {
            storePage = storeRepository.findAllByCompanyEmail(customUserDetails.getEmail(), PageRequest.of(page, size));
        }
        if (storePage.isEmpty()) {
            throw new BaseException(BaseResponseMessage.STORE_SEARCH_ALL_FAIL_NOT_FOUND);
        }

        // 2. Store DTO Page 반환
        Page<SearchStoreRes> searchStoreResPage = storePage.map(store -> {
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

    // 팝업 스토어 수정
    @Transactional
    public UpdateStoreRes update(CustomUserDetails customUserDetails, Long storeIdx, UpdateStoreReq dto, List<String> fileNames) throws BaseException {
        // 1. 예외: 팝업 스토어가 존재하지 않을때, 현재 사용자가 이메일이 다를때
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_UPDATE_FAIL_NOT_FOUND)
        );
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        // 2. 팝업 스토어 업데이트 / 스토어 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지를 저장
        store.update(
                dto.getStoreName(),
                dto.getStoreContent(),
                dto.getStoreAddress(),
                dto.getCategory(),
                dto.getTotalPeople(),
                dto.getStoreStartDate(),
                dto.getStoreEndDate()
        );
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
        return UpdateStoreRes.builder().storeIdx(store.getIdx()).build();
    }

    // 팝업 스토어 삭제
    @Transactional
    public void delete(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException{
        // 1. 예외: 팝업 스토어가 존재하지 않을때, 등록한 팝업스토어와 현재 인증 사용자의 이메일이 다를때
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.STORE_DELETE_FAIL_INVALID_MEMBER);
        }
        storeRepository.deleteById(storeIdx);
    }
}