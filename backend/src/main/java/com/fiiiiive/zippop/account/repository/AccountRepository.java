package com.fiiiiive.zippop.account.repository;

import com.fiiiiive.zippop.account.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserId(String userId);

    Optional<Account> findByEmail(String email);

    boolean existsByUserId(String userId);

}
