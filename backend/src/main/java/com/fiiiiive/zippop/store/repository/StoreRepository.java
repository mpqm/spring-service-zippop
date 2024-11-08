package com.fiiiiive.zippop.store.repository;

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
    // 팝업스토어 이름을 기반으로 팝업 스토어 조회
    Optional<Store> findByStoreName(String storeName);

    // 팝업스토어 전체 조회
    Page<Store> findAll(Pageable pageable);

    // 좋아요 증가/감소
    void incrementLikeCount(Long storeIdx);
    void decrementLikeCount(Long storeIdx);

    // 기업 인덱스를 기반으로 팝업 스토어 조회
    @Query("SELECT s FROM Store s WHERE s.companyEmail = :companyEmail")
    Optional<Page<Store>> findByCompanyEmail(String companyEmail, Pageable pageable);

    // 검색어 기반으로 전체 조회
    @Query("SELECT s FROM Store s " +
            "WHERE s.address LIKE %:keyword% " +
            "OR s.name LIKE %:keyword% " +
            "OR s.category LIKE %:keyword% " +
            "OR s.companyEmail LIKE %:keyword%")
    Page<Store> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 날짜 범위 기반으로 전체 조회
    @Query("SELECT s FROM Store s " +
            "WHERE (:startDate IS NULL OR s.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.endDate <= :endDate)")
    Page<Store> findByStoreDateRange (LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}