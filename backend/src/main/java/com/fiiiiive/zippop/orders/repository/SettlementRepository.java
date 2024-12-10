package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}