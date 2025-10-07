package com.fiiiiive.zippop.payout.repository;

import com.fiiiiive.zippop.payout.entity.Payout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {

    @Query("SELECT p FROM Payout p WHERE p.store.idx = :storeIdx")
    Optional<Page<Payout>> findAllByStoreIdx(Long storeIdx, Pageable pageable);

}