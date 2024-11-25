package com.fiiiiive.zippop.cart.repository;

import com.fiiiiive.zippop.cart.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    // 카트 아이템 인덱스. 고객 회원 인덱스로 조회
    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.cart ca " +
            "JOIN FETCH ca.customer cu " +
            "WHERE ci.idx = :cartItemIdx AND cu.idx = :customerIdx")
    Optional<CartItem> findByCartItemIdxAndCustomerIdx(Long cartItemIdx, Long customerIdx);

    // 카트 인덱스, 굿즈 인덱스로 조회
    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.cart ca " +
            "JOIN FETCH ci.goods go " +
            "WHERE ca.idx = :cartIdx AND go.idx = :GoodsIdx")
    Optional<CartItem> findByGoodsIdxAndCartIdx(Long GoodsIdx, Long cartIdx);

    // 카트 아이템 인덱스로 수량 증가
    @Modifying
    @Query("UPDATE CartItem ci SET ci.count = ci.count + 1 WHERE ci.idx = :cartItemIdx")
    void incrementCount(Long cartItemIdx);

    // 카트 아이템 인덱스로 수량 감소
    @Modifying
    @Query("UPDATE CartItem ci SET ci.count = ci.count - 1 WHERE ci.idx = :cartItemIdx AND ci.count > 0")
    void decrementCount(Long cartItemIdx);

    // 카트 아이템, 고객 인덱스로 삭제
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.idx = :cartItemIdx AND ci.cart.customer.idx = :customerIdx")
    void deleteByCartItemIdxAndCustomerIdx(Long cartItemIdx, Long customerIdx);

}
