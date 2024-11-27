package com.fiiiiive.zippop.store.repository;

import com.fiiiiive.zippop.store.model.entity.StoreImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    // 스토어 인덱스로 전체 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM StoreImage si " +
            "WHERE si.store.idx = :storeIdx")
    void deleteAllByStoreIdx(@Param("storeIdx") Long storeIdx);
}
