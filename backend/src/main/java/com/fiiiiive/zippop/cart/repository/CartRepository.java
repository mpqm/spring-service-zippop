package com.fiiiiive.zippop.cart.repository;

import com.fiiiiive.zippop.cart.model.Cart;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    // 고객 회원 인덱스, 팝업 인덱스로 조회
    @Query("SELECT c " +
            "FROM Cart c " +
            "JOIN FETCH c.customer cc " +
            "JOIN FETCH c.popup cp " +
            "WHERE cc.idx = :customerIdx AND cp.idx = :popupIdx")
    Optional<Cart> findByCustomerIdxAndPopupIdx(@Param("customerIdx") Long customerIdx, @Param("popupIdx") Long popupIdx);

    // 장바구니 인덱스로 조회
    @Query("SELECT c " +
            "FROM Cart c " +
            "WHERE c.idx = :cartIdx")
    Optional<Cart> findByCartIdx(@Param("cartIdx") Long cartIdx);

    // 고객 회원 인덱스로 목록 조회
    @Query("SELECT c " +
            "FROM Cart c " +
            "JOIN FETCH c.customer cc " +
            "WHERE cc.idx = :customerIdx")
    Optional<Page<Cart>> findAllByCustomerIdx(@Param("customerIdx") Long customerIdx, Pageable pageable);

}
