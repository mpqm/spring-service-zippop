package com.fiiiiive.zippop.commerce.repository;

import com.fiiiiive.zippop.commerce.model.entity.Orders;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    // 고객 인덱스로 목록 조회
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer oc " +
            "WHERE oc.idx = :customerIdx")
    Optional<Page<Orders>> findAllByCustomerIdx(@Param("customerIdx") Long customerIdx, Pageable pageable);

    // 스토어 인덱스로 목록 조회
    @Query("SELECT o FROM Orders o " +
            "WHERE o.storeIdx = :storeIdx")
    Optional<Page<Orders>> findAllByStoreIdx(@Param("storeIdx") Long storeIdx, Pageable pageable);

    // 주문, 고객 인덱스로 조회
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer oc " +
            "WHERE o.idx = :ordersIdx AND oc.idx = :customerIdx ")
    Optional<Orders> findByOrdersIdxAndCustomerIdx(@Param("ordersIdx") Long ordersIdx, @Param("customerIdx") Long customerIdx);

    // 주문, 스토어 인덱스로 조회
    @Query("SELECT o FROM Orders o " +
            "WHERE o.idx = :ordersIdx AND o.storeIdx = :storeIdx")
    Optional<Orders> findByOrdersIdxAndStoreIdx(@Param("ordersIdx") Long ordersIdx, @Param("storeIdx") Long storeIdx);

    // 주문, 스토어 인덱스 및 주문 상태로 조회
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer oc " +
            "WHERE o.storeIdx = :storeIdx AND oc.idx = :customerIdx AND o.status LIKE %:status%")
    Optional<Orders> findByStoreIdxAndCustomerIdxAndStatus(@Param("storeIdx") Long storeIdx, @Param("customerIdx") Long customerIdx, @Param("status") String status);

    // 주문 상태 및 결제 완료 날짜로 조회
    @Query("SELECT o FROM Orders o WHERE o.status LIKE %:status AND FUNCTION('DATE', o.updatedAt) = :updatedAt")
    List<Orders> findByStatusAndUpdatedAt(@Param("status") String status, @Param("updatedAt") LocalDate updatedAt);


}