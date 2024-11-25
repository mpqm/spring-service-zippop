package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.StoreLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    // 고객, 스토어 인덱스로 조회
    @Query("SELECT sl FROM StoreLike sl " +
            "JOIN FETCH sl.customer " +
            "JOIN FETCH sl.store " +
            "WHERE sl.customer.idx = :customerIdx AND sl.store.idx = :storeIdx")
    Optional<StoreLike> findByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);

    // 고객 인덱스로 페이징 조회
    @Query("SELECT sl FROM StoreLike sl " +
            "JOIN FETCH sl.customer " +
            "JOIN FETCH sl.store " +
            "WHERE sl.customer.idx = :customerIdx")
    Optional<Page<StoreLike>> findAllByCustomerIdx(Long customerIdx, Pageable pageable);

    // 고객, 스토어 인덱스로 삭제
    @Modifying
    @Query("DELETE FROM StoreLike sl " +
            "WHERE sl.customer.idx = :customerIdx AND sl.store.idx = :storeIdx")
    void deleteByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);
}
