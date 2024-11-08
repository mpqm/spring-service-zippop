package com.fiiiiive.zippop.member.repository;

import com.fiiiiive.zippop.member.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("SELECT cp FROM Company cp WHERE cp.email = :companyEmail")
    Optional<Company> findByCompanyEmail(String companyEmail);

    @Query("SELECT cp FROM Company cp WHERE cp.idx = :companyIdx")
    Optional<Company> findByCompanyIdx(Long companyIdx);
}
