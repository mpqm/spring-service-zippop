package com.fiiiiive.zippop.account.repository;

import com.fiiiiive.zippop.account.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c JOIN FETCH c.account WHERE c.idx = :companyIdx")
    Optional<Company> findByCompanyIdx(@Param("companyIdx") Long companyIdx);

    Optional<Company> findByAccountIdx(Long accountIdx);

}
