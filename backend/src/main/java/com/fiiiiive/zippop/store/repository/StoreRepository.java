package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.*;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // 팝업스토어 인덱스를 기반으로 팝업 스토어 조회
    @Query("SELECT s FROM Store s WHERE s.idx = :storeIdx")
    Optional<Store> findByStoreIdx(Long storeIdx);

    // 팝업스토어 이름을 기반으로 팝업 스토어 조회
    @Query("SELECT s FROM Store s WHERE s.name = :storeName")
    Optional<Store> findByStoreName(String storeName);

    // 팝업스토어 전체 조회
    Page<Store> findAll(Pageable pageable);

    // 좋아요 증가
    @Modifying
    @Query("UPDATE Store s SET s.likeCount = s.likeCount + 1 WHERE s.idx = :storeIdx")
    void incrementLikeCount(Long storeIdx);
    // 좋아요 감소
    @Modifying
    @Query("UPDATE Store s SET s.likeCount = s.likeCount - 1 WHERE s.idx = :storeIdx AND s.likeCount > 0")
    void decrementLikeCount(Long storeIdx);

    // 기업 인덱스를 기반으로 팝업 스토어 조회
    @Query("SELECT s FROM Store s WHERE s.companyEmail = :companyEmail")
    Page<Store> findByCompanyEmail(String companyEmail, Pageable pageable);

    // 검색어 기반으로 전체 조회
    @Query("SELECT s FROM Store s " +
            "WHERE s.address LIKE %:keyword% " +
            "OR s.name LIKE %:keyword% " +
            "OR s.category LIKE %:keyword% " +
            "OR s.startDate LIKE %:keyword% " +
            "OR s.companyEmail LIKE %:keyword%")
    Page<Store> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 검색어 기반으로 전체 조회(기업용)
    @Query("SELECT s FROM Store s " +
            "WHERE s.companyEmail = :companyEmail " +
            "AND (s.address LIKE %:keyword% " +
            "OR s.name LIKE %:keyword% " +
            "OR s.category LIKE %:keyword% " +
            "OR s.startDate LIKE %:keyword%)")
    Page<Store> findByKeywordAndCompanyEmail(@Param("keyword") String keyword, @Param("companyEmail") String companyEmail, Pageable pageable);

}