package com.fiiiiive.zippop.goods.repository;

import com.fiiiiive.zippop.goods.model.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Page<Goods> findAll(Pageable pageable);

    Optional<Page<Goods>> findByStoreIdx(Long storeIdx, Pageable pageable);

    // 비관적락 잠금 설정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Goods g WHERE g.name = :goodsIdx")
    Optional<Goods> findByGoodsIdx(Long goodsIdx);
}
