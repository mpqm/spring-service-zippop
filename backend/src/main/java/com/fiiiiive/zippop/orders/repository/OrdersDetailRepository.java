package com.fiiiiive.zippop.orders.repository;


import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> { }