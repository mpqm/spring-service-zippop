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

    // 고객 인덱스로 목록 조회
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer c " +
            "WHERE c.idx = :customerIdx")
    Optional<Page<Orders>> findAllByCustomerIdx(Long customerIdx, Pageable pageable);

    // 스토어 인덱스로 목록 조회
    Optional<Page<Orders>> findAllByStoreIdx(Long storeIdx, Pageable pageable);

    // 주문, 고객 인덱스로 조회
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer c " +
            "WHERE c.idx = :customerIdx AND o.idx = :ordersIdx")
    Optional<Orders> findByOrdersIdxAndCustomerIdx(Long ordersIdx, Long customerIdx);

    // 주문, 스토어 인덱스로 조회
    @Query("SELECT o FROM Orders o WHERE o.idx = :ordersIdx AND o.storeIdx = :storeIdx")
    Optional<Orders> findByOrdersIdxAndStoreIdx(Long ordersIdx, Long storeIdx);

    // 주문, 스토어 인덱스 및 주문 상태로 조회
    @Query("SELECT o FROM Orders o WHERE o.storeIdx = :storeIdx AND o.customer.idx = :customerIdx AND o.status LIKE %:status%")
    Optional<Orders> findByStoreIdxAndCustomerIdxAndStatus(Long storeIdx, Long customerIdx, String status);


}