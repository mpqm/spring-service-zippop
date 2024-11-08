package com.fiiiiive.zippop.store;

import com.fiiiiive.zippop.store.model.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.*;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // 팝업스토어 인덱스를 기반으로 팝업 스토어 조회
    Optional<Store> findByStoreIdx(Long storeIdx);

    // 기업 인덱스를 기반으로 팝업 스토어 조회
    Optional<Page<Store>> findByCompanyEmail(String companyEmail, Pageable pageable);
    @Query("SELECT ps FROM Store ps JOIN FETCH ps.popupGoodsList WHERE ps.company.companyIdx = :companyIdx")
    Page<Store> findByCompanyIdxFetchJoin(Long companyIdx, Pageable pageable);

    // 검색어 기반으로 전체 조회
    @Query("SELECT ps FROM Store ps " +
            "WHERE ps.storeAddress LIKE %:keyword% " +
            "OR ps.storeName LIKE %:keyword% " +
            "OR ps.category LIKE %:keyword% " +
            "OR ps.companyEmail LIKE %:keyword%")
    Optional<Page<Store>> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 날짜 범위 기반으로 전체 조회
    @Query("SELECT ps FROM Store ps " +
            "WHERE (:startDate IS NULL OR ps.storeStartDate >= :storeStartDate) " +
            "AND (:endDate IS NULL OR ps.storeEndDate <= :storeEndDate)")
    Optional<Page<Store>> findByStoreDateRange (LocalDateTime storeStartDate, LocalDateTime storeEndDate, Pageable pageable);

    // 팝업스토어 이름을 기반으로 팝업 스토어 조회
    Optional<Store> findByStoreName(String storeName);

    @Query("SELECT ps FROM Store ps JOIN FETCH ps.popupGoodsList WHERE ps.storeName = :storeName")
    Optional<Store> findByStoreNameFetchJoin(String storeName);

    Page<Store> findByStoreAddress(String storeAddress, Pageable pageable);

    @Query("SELECT ps FROM Store ps JOIN FETCH ps.popupGoodsList WHERE ps.storeAddress = :storeAddress")
    Page<Store> findByStoreAddressFetchJoin(String storeAddress, Pageable pageable);

    @Query("SELECT ps FROM Store ps JOIN FETCH ps.popupGoodsList WHERE ps.storeEndDate = :storeDate")
    Page<Store> findByStoreEndDateFetchJoin(String storeDate, Pageable pageable);

    Optional<Page<Store>> findByCategory(String category, Pageable pageable);

    @Query("SELECT ps FROM Store ps JOIN FETCH ps.popupGoodsList WHERE ps.category = :category")
    Page<Store> findByCategoryFetchJoin(String category, Pageable pageable);
}