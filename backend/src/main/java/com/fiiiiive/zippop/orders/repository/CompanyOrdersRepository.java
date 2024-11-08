package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.CompanyOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyOrdersRepository extends JpaRepository<CompanyOrders, Long> {
    @Query("SELECT co FROM CompanyOrders co " +
            "JOIN FETCH co.company c " +
            "WHERE c.idx = :companyIdx")
    Optional<List<CompanyOrders>> findByCompanyIdx(Long companyIdx);
}