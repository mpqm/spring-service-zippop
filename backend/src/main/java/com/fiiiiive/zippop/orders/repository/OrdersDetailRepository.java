package com.fiiiiive.zippop.orders.repository;


import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {

    @Query("""
        SELECT COUNT(od) > 0 FROM OrdersDetail od
        JOIN od.orders o
        JOIN od.goods g
        WHERE o.customer.idx = :customerIdx
        AND g.popup.idx = :popupIdx
        AND o.status IN :statuses
    """)
    boolean existsReviewableOrder(Long customerIdx, Long popupIdx, List<OrdersStatus> statuses);

}
