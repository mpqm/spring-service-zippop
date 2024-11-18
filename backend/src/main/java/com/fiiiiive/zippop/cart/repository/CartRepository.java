package com.fiiiiive.zippop.cart.repository;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT c FROM Cart c WHERE c.customer.idx = :customerIdx")
    Optional<Cart> findByCustomerIdx(Long customerIdx);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customer.idx = :customerIdx")
    void deleteAllByCustomerIdx(Long customerIdx);
}
