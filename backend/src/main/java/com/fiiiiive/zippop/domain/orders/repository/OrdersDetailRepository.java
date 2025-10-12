package com.fiiiiive.zippop.domain.orders.repository;


import com.fiiiiive.zippop.domain.orders.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> { }