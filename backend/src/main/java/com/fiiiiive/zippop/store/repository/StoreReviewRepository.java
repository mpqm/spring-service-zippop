package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.StoreReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
    // 팝업 스토어 인덱스를 기반으로 스토어의 리뷰를 조회
    @Query("SELECT sr FROM StoreReview sr JOIN FETCH sr.store s WHERE s.idx = :storeIdx")
    Optional<Page<StoreReview>> findByStoreIdx(Long storeIdx, Pageable pageable);

    // 리뷰 단일 조회
    @Query("SELECT sr From StoreReview sr JOIN FETCH sr.customer c JOIN FETCH sr.store s WHERE c.idx = :customerIdx AND s.idx = :storeIdx")
    Optional<StoreReview> findByStoreIdxAndCustomerIdx(Long storeIdx, Long customerIdx);

    // 고객의 인덱스를 기반으로 자신이 쓴 리뷰를 전체 조회
    @Query("SELECT sr FROM StoreReview sr JOIN FETCH sr.customer c WHERE c.idx = :customerIdx")
    Optional<Page<StoreReview>> findAllByCustomerIdx(Long customerIdx, Pageable pageable);
}
