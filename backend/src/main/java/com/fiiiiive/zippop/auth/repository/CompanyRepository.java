package com.fiiiiive.zippop.auth.repository;

import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // 기업 회원 인덱스로 조회
    @Query("SELECT cp FROM Company cp WHERE cp.idx = :companyIdx")
    Optional<Company> findByCompanyIdx(Long companyIdx);

    // 기업 회원 아이디로 조회
    @Query("SELECT cp FROM Company cp WHERE cp.userId = :userId")
    Optional<Company> findByUserId(String userId);

    // 기업 회원 이메일로 조회
    @Query("SELECT cp FROM Company cp WHERE cp.email = :companyEmail")
    Optional<Company> findByCompanyEmail(String companyEmail);
}
