package com.fiiiiive.zippop.settlement.repository;

import com.fiiiiive.zippop.settlement.model.entity.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("SELECT s FROM Settlement s WHERE s.storeIdx = :storeIdx")
    Optional<Page<Settlement>> findAllByStoreIdx(Long storeIdx, Pageable pageable);
}