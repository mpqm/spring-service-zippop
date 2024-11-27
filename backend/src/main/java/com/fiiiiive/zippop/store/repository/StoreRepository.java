package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // 스토어 인덱스로 조회
    @Query("SELECT s FROM Store s " +
            "WHERE s.idx = :storeIdx")
    Optional<Store> findByStoreIdx(@Param("storeIdx") Long storeIdx);

    // 스토어 인덱스로 좋아요 증가
    @Modifying
    @Query("UPDATE Store s SET s.likeCount = s.likeCount + 1 " +
            "WHERE s.idx = :storeIdx")
    void incrementLikeCount(@Param("storeIdx") Long storeIdx);

    // 스토어 인덱스로 좋아요 감소
    @Modifying
    @Query("UPDATE Store s SET s.likeCount = s.likeCount - 1 " +
            "WHERE s.idx = :storeIdx AND s.likeCount > 0")
    void decrementLikeCount(@Param("storeIdx") Long storeIdx);

    // 기업 이메일로 목록 조회
    @Query("SELECT s FROM Store s " +
            "WHERE s.companyEmail = :companyEmail")
    Page<Store> findAllByCompanyEmail(@Param("companyEmail") String companyEmail, Pageable pageable);

    // 상태값으로 목록 조회
    @Query("SELECT s FROM Store s WHERE s.status = :status")
    Page<Store> findAllByStatus(@Param("status") String status, Pageable pageable);

    // 검색어, 상태 기반으로 목록 조회
    @Query("SELECT s FROM Store s " +
            "WHERE s.status = :status " +
            "AND (s.address LIKE %:keyword% OR s.name LIKE %:keyword% OR s.category LIKE %:keyword% OR s.startDate LIKE %:keyword% OR s.companyEmail LIKE %:keyword%)")
    Page<Store> findAllByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    // 검색어, 기업 이메일로 목록 조회
    @Query("SELECT s FROM Store s " +
            "WHERE s.companyEmail = :companyEmail " +
            "AND (s.address LIKE %:keyword% OR s.name LIKE %:keyword% OR s.category LIKE %:keyword% OR s.startDate LIKE %:keyword%)")
    Page<Store> findAllByKeywordAndCompanyEmail(@Param("keyword") String keyword, @Param("companyEmail") String companyEmail, Pageable pageable);

}