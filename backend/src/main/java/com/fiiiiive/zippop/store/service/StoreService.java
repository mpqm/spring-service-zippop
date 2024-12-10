package com.fiiiiive.zippop.store.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.model.dto.UpdateStoreReq;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.model.dto.CreateStoreReq;
import com.fiiiiive.zippop.store.model.dto.CreateStoreRes;
import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import com.fiiiiive.zippop.store.model.dto.UpdateStoreRes;
import com.fiiiiive.zippop.store.repository.StoreImageRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CompanyRepository companyRepository;
    private final StoreImageRepository storeImageRepository;

    /* 팝업 스토어 등록 */
    @Transactional
    public CreateStoreRes register(CustomUserDetails customUserDetails, CreateStoreReq dto, List<String> fileNameList) throws BaseException {
        // 기업 회원 조회 없으면 예외 반환 (기업 회원 인덱스)
        Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_REGISTER_FAIL_UNAUTHORIZED)
        );

        // Store 생성 및 저장
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

        // Store Image 생성 및 저장
        for(String fileName : fileNameList){
            StoreImage storeImage = StoreImage.builder()
                    .url(fileName)
                    .store(store)
                    .build();
            storeImageRepository.save(storeImage);
        }

        // Store 인덱스 반환
        return CreateStoreRes.builder().storeIdx(store.getIdx()).build();
    }

    /* 팝업 스토어 단일 조회 */
    public SearchStoreRes search(Long storeIdx) throws BaseException {
        // 팝업 스토어 조회 없으면 예외 반환 (팝업 스토어 인덱스)
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_SEARCH_FAIL_NOT_FOUND)
        );

        // Store Dto 반환
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

    /* 팝업 스토어 목록 (전체 회원, 상태, 검색어) 조회 */
    public Page<SearchStoreRes> searchAll(String status, String keyword, int page, int size) throws BaseException {
        // 팝업 스토어 목록 조회 없으면 예외 반환 (팝업 스토어 상태, 검색어, 페이징)
        // 참 : 검색어가 있는 경우, 상태에 따라 활성화된 또는 종료된 팝업 스토어를 검색어로 페이징 조회
        // 거짓 : 검색어가 없는 경우, 상태에 따라 활성화 또는 종료된 팝업 스토어를 페이징 조회
        Page<Store> storePage = (keyword != null)
                ? storeRepository.findAllByKeywordAndStatus(keyword, status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                : storeRepository.findAllByStatus(status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        if (storePage.isEmpty()) {
            throw new BaseException(BaseResponseMessage.STORE_SEARCH_ALL_FAIL_NOT_FOUND);
        }

        // Store Page DTO 반환
        return storePage.map(store -> {
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
    }

    /* 팝업 스토어 목록 (기업 회원, 검색어) 조회 */
    public Page<SearchStoreRes> searchAllAsCompany(CustomUserDetails customUserDetails, String keyword, int page, int size) throws BaseException {
        // 팝업 스토어 목록 조회 없으면 예외 반환 (등록된 기업회원 이메일, 키워드, 페이징)
        // 참 : 키워드(keyword)가 있는 경우, 등록된 기업회원의 이메일과 키워드로 페이징 조회
        // 거짓 : 키워드(keyword)가 없는 경우, 등록된 기업회원의 이메일로 페이징 조회
        Page<Store> storePage = (keyword != null)
            ? storeRepository.findAllByKeywordAndCompanyEmail(keyword, customUserDetails.getEmail(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
            : storeRepository.findAllByCompanyEmail(customUserDetails.getEmail(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        if (storePage.isEmpty()) {
            throw new BaseException(BaseResponseMessage.STORE_SEARCH_ALL_FAIL_NOT_FOUND);
        }

        // Store Page DTO 반환
        return storePage.map(store -> {
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
    }

    /* 팝업 스토어 수정 */
    @Transactional
    public UpdateStoreRes update(CustomUserDetails customUserDetails, Long storeIdx, UpdateStoreReq dto, List<String> fileNames) throws BaseException {
        // 팝업 스토어 조회 없으면 예외 반환 (팝업 스토어 인덱스, 기업 회원 이메일)
        Store store = storeRepository.findByStoreIdxAndCompanyEmail(storeIdx, customUserDetails.getEmail()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_UPDATE_FAIL_NOT_FOUND)
        );

        // 팝업 스토어 업데이트
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

        // 팝업 스토어 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지 저장
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

    /* 팝업 스토어 삭제 */
    @Transactional
    public void delete(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException{
        // 팝업 스토어 조회 없으면 예외 반환 (인덱스, 기업 회원 이메일)
        storeRepository.findByStoreIdxAndCompanyEmail(storeIdx, customUserDetails.getEmail()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );
        storeRepository.deleteById(storeIdx);
    }
}