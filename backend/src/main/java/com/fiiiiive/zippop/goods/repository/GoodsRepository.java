package com.fiiiiive.zippop.goods.repository;

import com.fiiiiive.zippop.goods.model.Goods;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    // 굿즈 인덱스로 조회
    // 비관적락 잠금 설정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Goods g " +
            "WHERE g.idx = :goodsIdx")
    Optional<Goods> findByGoodsIdx(@Param("goodsIdx") Long goodsIdx);

    // 굿즈, 팝업 인덱스로 조회
    @Query("SELECT g FROM Goods g " +
            "JOIN FETCH g.popup gp " +
            "WHERE g.idx = :goodsIdx  AND gp.idx = :popupIdx")
    Optional<Goods> findByGoodsIdxAndPopupIdx(@Param("goodsIdx") Long goodsIdx, @Param("popupIdx") Long popupIdx);

    // 팝업 인덱스로 조회
    @Query("SELECT g FROM Goods g " +
            "JOIN FETCH g.popup gp " +
            "WHERE gp.idx = :popupIdx")
    Page<Goods> findAllByPopupIdx(@Param("popupIdx") Long popupIdx, Pageable pageable);

    // 검색어, 팝업 인덱스로 전체 조회
    @Query("SELECT g FROM Goods g " +
            "JOIN FETCH g.popup gp " +
            "WHERE gp.idx = :popupIdx AND (gp.name LIKE %:keyword% OR g.name LIKE %:keyword%)")
    Page<Goods> findAllByPopupIdxAndKeyword(@Param("popupIdx") Long popupIdx, @Param("keyword") String keyword, Pageable pageable);

}
