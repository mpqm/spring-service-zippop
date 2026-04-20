package com.fiiiiive.zippop.cart.repository;

import com.fiiiiive.zippop.cart.model.entity.CartItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    // 카트 아이템 인덱스. 고객 회원 인덱스로 조회
    @Query("SELECT ci " +
            "FROM CartItem ci " +
            "JOIN FETCH ci.cart cic " +
            "JOIN FETCH cic.customer cicc " +
            "WHERE ci.idx = :cartItemIdx " +
            "AND cic.idx = :cartIdx " +
            "AND cicc.idx = :customerIdx")
    Optional<CartItem> findByCartIdxAndCartItemIdxAndCustomerIdx(@Param("cartIdx") Long cartIdx, @Param("cartItemIdx") Long cartItemIdx, @Param("customerIdx") Long customerIdx);

}
