package com.fiiiiive.zippop.goods.repository;

import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {
    // 굿즈 인덱스로 전체 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM GoodsImage gi " +
            "WHERE gi.goods.idx = :goodsIdx")
    void deleteAllByGoodsIdx(@Param("goodsIdx") Long goodsIdx);
}
