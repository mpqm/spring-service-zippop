package com.fiiiiive.zippop.commerce.repository;

import com.fiiiiive.zippop.commerce.model.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}