package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.Orders;
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

    // 팝업 인덱스로 목록 조회 (엔티티 관계: Orders.popup)
    @Query("SELECT o FROM Orders o WHERE o.popup.idx = :popupIdx")
    Optional<Page<Orders>> findAllByPopupIdx(@Param("popupIdx") Long popupIdx, Pageable pageable);

    // 주문, 고객 인덱스로 조회
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer oc " +
            "WHERE o.idx = :ordersIdx AND oc.idx = :customerIdx ")
    Optional<Orders> findByOrdersIdxAndCustomerIdx(@Param("ordersIdx") Long ordersIdx, @Param("customerIdx") Long customerIdx);

    // 주문, 팝업 인덱스로 조회 (엔티티 관계: Orders.popup)
    @Query("SELECT o FROM Orders o WHERE o.idx = :ordersIdx AND o.popup.idx = :popupIdx")
    Optional<Orders> findByOrdersIdxAndPopupIdx(@Param("ordersIdx") Long ordersIdx, @Param("popupIdx") Long popupIdx);


    // 주문 상태 및 업데이트일 기준 조회 (정산용, popup fetch)
    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.popup " +
            "WHERE (o.status = :status1 OR o.status = :status2) AND FUNCTION('DATE', o.updatedAt) = :updatedAt")
    List<Orders> findByStatusAndUpdatedAt(@Param("status1") String status1, @Param("status2") String status2, @Param("updatedAt") LocalDate updatedAt);


}