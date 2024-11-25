package com.fiiiiive.zippop.orders.repository;


import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> { }