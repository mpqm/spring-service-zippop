package com.fiiiiive.zippop.orders;

import com.fiiiiive.zippop.orders.model.entity.CompanyOrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyOrdersDetailRepository extends JpaRepository<CompanyOrdersDetail, Long> { }
