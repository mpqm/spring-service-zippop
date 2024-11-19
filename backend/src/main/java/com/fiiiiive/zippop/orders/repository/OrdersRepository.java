package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.customer c " +
            "WHERE c.idx = :customerIdx")
    Optional<List<Orders>> findByCustomerIdx(Long customerIdx);
}