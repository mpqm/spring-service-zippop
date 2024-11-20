package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer c " +
            "WHERE c.idx = :customerIdx")
    Page<Orders> findAllByCustomerIdx(Long customerIdx, Pageable pageable);

    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer c " +
            "WHERE c.idx = :customerIdx AND o.idx = :ordersIdx")
    Optional<Orders> findByOrdersIdxAndCustomerIdx(Long ordersIdx, Long customerIdx);

    @Query("SELECT o FROM Orders o WHERE o.idx = :ordersIdx AND o.storeIdx = :storeIdx")
    Optional<Orders> findByOrdersIdxAndStoreIdx(Long ordersIdx, Long storeIdx);

    Optional<Page<Orders>> findAllByStoreIdx(Long storeIdx, Pageable pageable);
}