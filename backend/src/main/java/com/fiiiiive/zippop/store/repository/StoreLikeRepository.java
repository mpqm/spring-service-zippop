package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    @Query("SELECT psl FROM StoreLike psl " +
            "JOIN FETCH psl.customer " +
            "JOIN FETCH psl.store " +
            "WHERE psl.customer.idx = :customerIdx AND psl.store.idx = :storeIdx")
    Optional<StoreLike> findByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);

    @Modifying
    @Query("DELETE FROM StoreLike psl " +
            "WHERE psl.customer.idx = :customerIdx AND psl.store.idx = :storeIdx")
    void deleteByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);
}
