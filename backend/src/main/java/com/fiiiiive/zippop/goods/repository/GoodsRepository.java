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

    @Query("SELECT pg FROM PopupGoods pg JOIN FETCH pg.store ps WHERE ps.storeIdx = :storeIdx")
    Optional<Page<Goods>> findByStoreIdx(Long storeIdx, Pageable pageable);

    @Query("SELECT pg FROM PopupGoods pg JOIN FETCH pg.store")
    Page<Goods> findAllFetchJoin(Pageable pageable);

    Page<Goods> findByProductPrice(Integer productPrice, Pageable pageable);

    @Query("SELECT pg FROM PopupGoods pg JOIN FETCH pg.store WHERE pg.productPrice = :productPrice")
    Page<Goods> findByProductPriceFetchJoin(Integer productPrice, Pageable pageable);

    Optional<Page<Goods>> findByProductName(String productName, Pageable pageable);

    Optional<Goods> findByProductIdx(Long productIdx);
//    @Query("SELECT pg FROM PopupGoods pg JOIN FETCH pg.store WHERE pg.productName = :productName")
//    Page<PopupGoods> findByProductNameFetchJoin(String productName, Pageable pageable);
//
//    public Page<PopupGoods> findByStoreName(String storeName, Pageable pageable);
//
//    @Query("SELECT pg FROM PopupGoods pg JOIN FETCH pg.store ps WHERE ps.storeName = :storeName")
//    Page<PopupGoods> findByStoreNameFetchJoin(String storeName, Pageable pageable);

    // 비관적락 잠금 설정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pg FROM PopupGoods pg WHERE pg.productIdx = :productIdx")
    Optional<Goods> findByIdx(@Param("productIdx") Long productIdx);
}
