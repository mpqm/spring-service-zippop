package com.fiiiiive.zippop.goods.repository;

import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.store.model.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Page<Goods> findAll(Pageable pageable);

    Page<Goods> findByStoreIdx(Long storeIdx, Pageable pageable);

    // 비관적락 잠금 설정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Goods g WHERE g.idx = :goodsIdx")
    Optional<Goods> findByGoodsIdx(Long goodsIdx);

    // 검색어 기반으로 전체 조회 => 재고 굿즈 용
    @Query("SELECT g FROM Goods g JOIN FETCH g.store gs " +
            "WHERE gs.name LIKE %:keyword% " +
            "OR g.name LIKE %:keyword%")
    Page<Goods> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 검색어 + 팝업스토어 인덱스로 전체 조회
    @Query("SELECT g FROM Goods g JOIN FETCH g.store gs " +
            "WHERE gs.idx = :storeIdx AND (gs.name LIKE %:keyword% " +
            "OR g.name LIKE %:keyword%)")
    Page<Goods> findByStoreIdxAndKeyword(Long storeIdx, @Param("keyword") String keyword, Pageable pageable);
}
