package com.fiiiiive.zippop.auth.repository;

import com.fiiiiive.zippop.auth.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // 고객 회원 인덱스로 조회
    @Query("SELECT cm FROM Customer cm " +
            "WHERE cm.idx = :customerIdx")
    Optional<Customer> findByCustomerIdx(@Param("customerIdx") Long customerIdx);

    // 고객 회원 아이디로 조회
    @Query("SELECT cm FROM Customer cm " +
            "WHERE cm.userId = :userId")
    Optional<Customer> findByUserId(@Param("userId") String userId);

    // 고객 회원 이메일로 조회
    @Query("SELECT cm FROM Customer cm " +
            "WHERE cm.email = :customerEmail")
    Optional<Customer> findByCustomerEmail(@Param("customerEmail") String customerEmail);
}
