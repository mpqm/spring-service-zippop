package com.fiiiiive.zippop.member.repository;

import com.fiiiiive.zippop.member.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT cm FROM Customer cm WHERE cm.idx = :customerIdx")
    Optional<Customer> findByCustomerIdx(Long customerIdx);

    @Query("SELECT cm FROM Customer cm WHERE cm.email = :customerEmail")
    Optional<Customer> findByCustomerEmail(String customerEmail);

}
