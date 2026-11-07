package com.fiiiiive.zippop.account.repository;

import com.fiiiiive.zippop.account.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Customer c JOIN FETCH c.account WHERE c.idx = :customerIdx")
    Optional<Customer> findForUpdate(@Param("customerIdx") Long customerIdx);

    @Query("SELECT c FROM Customer c JOIN FETCH c.account WHERE c.idx = :customerIdx")
    Optional<Customer> findByCustomerIdx(@Param("customerIdx") Long customerIdx);

    Optional<Customer> findByAccountIdx(Long accountIdx);

}
