package com.fiiiiive.zippop.cart.repository;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    // 고객 회원 인덱스, 스토어 인덱스로 조회
    @Query("SELECT c FROM Cart c " +
            "JOIN FETCH c.customer cc " +
            "JOIN FETCH c.store cs " +
            "WHERE cc.idx = :customerIdx AND cs.idx = :storeIdx")
    Optional<Cart> findByCustomerIdxAndStoreIdx(@Param("customerIdx") Long customerIdx, @Param("storeIdx") Long storeIdx);

    // 고객 회원 인덱스로 목록 조회
    @Query("SELECT c FROM Cart c " +
            "JOIN FETCH c.customer cc " +
            "WHERE cc.idx = :customerIdx")
    Optional<Page<Cart>> findAllByCustomerIdxAndStoreIdx(@Param("customerIdx") Long customerIdx, Pageable pageable);
}
