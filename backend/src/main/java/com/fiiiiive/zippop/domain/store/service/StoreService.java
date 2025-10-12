package com.fiiiiive.zippop.domain.store.service;

import com.fiiiiive.zippop.domain.auth.entity.Customer;
import com.fiiiiive.zippop.domain.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.domain.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.domain.auth.entity.Company;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.domain.orders.entity.Orders;
import com.fiiiiive.zippop.domain.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.domain.store.dto.StoreDto;
import com.fiiiiive.zippop.domain.store.entity.Store;
import com.fiiiiive.zippop.domain.store.entity.StoreLike;
import com.fiiiiive.zippop.domain.store.entity.StoreReview;
import com.fiiiiive.zippop.domain.store.repository.StoreImageRepository;
import com.fiiiiive.zippop.domain.store.repository.StoreLikeRepository;
import com.fiiiiive.zippop.domain.store.repository.StoreRepository;
import com.fiiiiive.zippop.domain.store.repository.StoreReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreImageRepository storeImageRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final OrdersRepository ordersRepository;
    private final StoreReviewRepository storeReviewRepository;

    // 스토어 등록
    @Transactional
    public StoreDto.CreateStoreRes registerStore(CustomUserDetails customUserDetails, StoreDto.CreateStoreReq dto, List<String> urls) throws BaseException {

        // 기업 회원 조회(companyIdx)
        Company company = companyRepository.findByCompanyIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REGISTER_FAIL_UNAUTHORIZED)
        );

        // Store 생성
        Store store = dto.toEntity(customUserDetails, company);
        storeRepository.save(store);

        // Store Image 생성
        for(String url : urls) {
            storeImageRepository.save(StoreDto.CreateStoreImageReq.toEntity(store, url));
        }

        return StoreDto.CreateStoreRes.builder().storeIdx(store.getIdx()).build();

    }

    // 스토어 조회
    public StoreDto.SearchStoreRes searchStore(Long storeIdx) throws BaseException {

        // 스토어 조회(storeIdx)
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_SEARCH_FAIL_NOT_FOUND)
        );

        return store.toDto();

    }

    // 스토어 목록 조회
    public Page<StoreDto.SearchStoreRes> searchAllStore(String status, String keyword, int page, int size) throws BaseException {

        // 스토어 페이지 조회(keyword, status, pageable) 조회
        // true : 검색어가 있는 경우, 상태에 따라 활성화된 또는 종료된 팝업 스토어를 검색어로 페이징 조회
        // false : 검색어가 없는 경우, 상태에 따라 활성화 또는 종료된 팝업 스토어를 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Store> storePage = (keyword != null)
                ? storeRepository.findAllByKeywordAndStatus(keyword, BaseStatus.valueOf(status), pageable)
                : storeRepository.findAllByStatus(BaseStatus.valueOf(status), pageable);

        // DTO 반환 (데이터가 없어도 빈 페이지 반환)
        return Store.toDtoPage(storePage);

    }

    // 스토어 목록 조회(기업용)
    public Page<StoreDto.SearchStoreRes> searchAllStoreAsCompany(CustomUserDetails customUserDetails, String keyword, int page, int size) throws BaseException {

        // 스토어 페이지 조회(keyword, email, pageable) 조회
        // true : 키워드(keyword)가 있는 경우, 등록된 기업회원의 이메일과 키워드로 페이징 조회
        // false : 키워드(keyword)가 없는 경우, 등록된 기업회원의 이메일로 페이징 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Store> storePage = (keyword != null)
            ? storeRepository.findAllByKeywordAndCompanyEmail(keyword, customUserDetails.getEmail(), pageable)
            : storeRepository.findAllByCompanyEmail(customUserDetails.getEmail(), pageable);

        return Store.toDtoPage(storePage);

    }

    // 스토어 수정
    @Transactional
    public StoreDto.UpdateStoreRes updateStore(CustomUserDetails customUserDetails, Long storeIdx, StoreDto.UpdateStoreReq dto, List<String> urls) throws BaseException {

        // 팝업 스토어 조회(storeIdx, email)
        Store store = storeRepository.findByStoreIdxAndCompanyEmail(storeIdx, customUserDetails.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_UPDATE_FAIL_NOT_FOUND)
        );

        // 팝업 스토어 수정
        storeRepository.save(store.update(dto));

        // 팝업 스토어 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지 저장
        if(urls != null){
            storeImageRepository.deleteAllByStoreIdx(storeIdx);
            for (String url : urls) storeImageRepository.save(StoreDto.CreateStoreImageReq.toEntity(store, url));
        }

        // DTO 반환
        return StoreDto.UpdateStoreRes.builder().storeIdx(store.getIdx()).build();

    }

    // 스토어 삭제
    @Transactional
    public void deleteStore(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException{

        // 스토어 조회(storeIdx, email)
        storeRepository.findByStoreIdxAndCompanyEmail(storeIdx, customUserDetails.getEmail()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );

        // 스토어 삭제
        storeRepository.deleteById(storeIdx);

    }

    // 스토어 좋아요 증감
    @Transactional
    public void registerStoreLike(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {

        // 팝업 스토어 인덱스로 조회 없으면 예외 반환
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_LIKE_FAIL_NOT_FOUND)
        );
        // 고객 회원 인덱스로 조회 없으면 예외 반환
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_LIKE_FAIL_INVALID_MEMBER)
        );

        // 좋아요 증감
        // if : 이미 좋아요를 누른 상태면 좋아요 삭제 / 스토어 좋아요 개수 감소(직접 쿼리 활용)
        // else : 좋아요를 처음 누르면 좋아요 저장 / 스토어 좋아요 개수 증가(직접 쿼리 활용)
        Optional<StoreLike> storeLikeOpt = storeLikeRepository.findByCustomerIdxAndStoreIdx(customer.getIdx(),storeIdx);
        if (storeLikeOpt.isPresent()) {
            storeLikeRepository.deleteByCustomerIdxAndStoreIdx(customer.getIdx(), storeIdx);
            storeRepository.decrementLikeCount(storeIdx);
        } else {
            storeLikeRepository.save(StoreDto.CreateStoreLikeReq.toEntity(store, customer));
            storeRepository.incrementLikeCount(storeIdx);
        }

    }

    // 스토어 좋아요 목록 조회(고객용)
    public Page<StoreDto.SearchStoreLikeRes> searchAllStoreLike(CustomUserDetails customUserDetails, int page, int size) throws BaseException {

        // 스토어 페이지 조회(customerIdx)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StoreLike> storeLikePage = storeLikeRepository.findAllByCustomerIdx(customUserDetails.getIdx(), pageable).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_LIKE_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return StoreLike.toDtoPage(storeLikePage);

    }

    // 스토어 리뷰 등록
    @Transactional
    public StoreDto.CreateStoreReviewRes registerStoreReview(CustomUserDetails customUserDetails, Long storeIdx, StoreDto.CreateStoreReviewReq dto) throws BaseException {

        // 결제 조회(storeIdx, customerIdx, 결제 완료 상태) / 결제한 사람만 리뷰 작성 가능
        Orders orders = ordersRepository.findByStoreIdxAndCustomerIdxAndStatus(storeIdx, customUserDetails.getIdx(), BaseStatus.valueOf("STOCK_COMPLETE"), BaseStatus.valueOf("RESERVE_COMPLETE")).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REVIEW_FAIL_INVALID_MEMBER)
        );

        // 스토어 조회 (storeIdx)
        Store store = storeRepository.findById(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REVIEW_FAIL_NOT_FOUND)
        );

        // 스토어 리뷰 조회(storeIdx, customerIdx) / 스토어 하나당 한개의 리뷰 작성 가능
        Optional<StoreReview> storeReviewOpt = storeReviewRepository.findByStoreIdxAndCustomerIdx(storeIdx, customUserDetails.getIdx());
        if(storeReviewOpt.isPresent()) throw new BaseException(BaseMessage.STORE_REVIEW_FAIL_DUPLICATED);


        // 스토어 리뷰 저장
        StoreReview storeReview = dto.toEntity(customUserDetails.getEmail(), store, orders);
        storeReviewRepository.save(storeReview);

        return StoreDto.CreateStoreReviewRes.builder().reviewIdx(storeReview.getIdx()).build();

    }

    // 스토어 리뷰 목록 조회
    public Page<StoreDto.SearchStoreReviewRes> searchAllStoreReview(Long storeIdx, int page, int size) throws BaseException {

        // 리뷰 조회(storeIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StoreReview> storeReviewPage = storeReviewRepository.findAllByStoreIdx(storeIdx, pageable).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REVIEW_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return StoreReview.toDtoPage(storeReviewPage);

    }

    // 스토어 리뷰 목록 조회(고객용)
    public Page<StoreDto.SearchStoreReviewRes> searchAllStoreReviewAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {

        // 리뷰 목록 조회(customerIdx, pageable)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StoreReview> storeReviewPage = storeReviewRepository.findAllByCustomerIdx(customUserDetails.getIdx(), pageable).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_REVIEW_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return StoreReview.toDtoPage(storeReviewPage);

    }

}