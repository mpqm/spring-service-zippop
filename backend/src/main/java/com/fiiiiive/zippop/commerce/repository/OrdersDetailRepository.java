package com.fiiiiive.zippop.commerce.repository;


import com.fiiiiive.zippop.commerce.model.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> { }