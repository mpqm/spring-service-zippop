package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.CustomerOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrdersRepository extends JpaRepository<CustomerOrders, Long> {
    @Query("SELECT co FROM CustomerOrders co " +
            "JOIN FETCH co.customer c " +
            "WHERE c.idx = :customerIdx")
    Optional<List<CustomerOrders>> findByCustomerIdx(Long customerIdx);
}