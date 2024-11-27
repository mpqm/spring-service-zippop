package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.StoreReview;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
    // 리뷰 인덱스로 조회
    @Query("SELECT sr From StoreReview sr " +
            "JOIN FETCH sr.customer src JOIN FETCH sr.store srs " +
            "WHERE src.idx = :customerIdx AND srs.idx = :storeIdx")
    Optional<StoreReview> findByStoreIdxAndCustomerIdx(@Param("storeIdx") Long storeIdx, @Param("customerIdx") Long customerIdx);

    // 스토어 인덱스로 목록 조회
    @Query("SELECT sr FROM StoreReview sr " +
            "JOIN FETCH sr.store srs " +
            "WHERE srs.idx = :storeIdx")
    Optional<Page<StoreReview>> findAllByStoreIdx(@Param("storeIdx") Long storeIdx, Pageable pageable);

    // 고객의 인덱스로 목록 조회
    @Query("SELECT sr FROM StoreReview sr " +
            "JOIN FETCH sr.customer src " +
            "WHERE src.idx = :customerIdx")
    Optional<Page<StoreReview>> findAllByCustomerIdx(@Param("customerIdx") Long customerIdx, Pageable pageable);
}
