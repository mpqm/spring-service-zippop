package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    @Query("SELECT sl FROM StoreLike sl " +
            "JOIN FETCH sl.customer " +
            "JOIN FETCH sl.store " +
            "WHERE sl.customer.idx = :customerIdx AND sl.store.idx = :storeIdx")
    Optional<StoreLike> findByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);

    @Query("SELECT sl FROM StoreLike sl " +
            "JOIN FETCH sl.customer " +
            "JOIN FETCH sl.store " +
            "WHERE sl.customer.idx = :customerIdx")
    Optional<List<StoreLike>> findAllByCustomerIdx(Long customerIdx);

    @Modifying
    @Query("DELETE FROM StoreLike sl " +
            "WHERE sl.customer.idx = :customerIdx AND sl.store.idx = :storeIdx")
    void deleteByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);
}
