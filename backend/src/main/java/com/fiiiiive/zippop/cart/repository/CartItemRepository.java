package com.fiiiiive.zippop.cart.repository;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    // 수량 증가 쿼리
    @Modifying
    @Query("UPDATE CartItem ci SET ci.count = ci.count + 1 WHERE ci.idx = :cartItemIdx")
    int incrementCount(Long cartItemIdx);

    // 수량 감소 쿼리
    @Modifying
    @Query("UPDATE CartItem ci SET ci.count = ci.count - 1 WHERE ci.idx = :cartItemIdx AND ci.count > 0")
    int decrementCount(Long cartItemIdx);


    @Query("SELECT ci FROM CartItem WHERE ci.idx = :cartItemIdx")
    Optional<CartItem> findByCartItemIdx(Long cartItemIdx);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.idx = :cartItemIdx")
    void deleteByCartItemIdx(Long cartItemIdx);
}
