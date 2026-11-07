package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.PaymentIntent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
    Optional<PaymentIntent> findByMerchantUid(String merchantUid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentIntent p where p.merchantUid = :merchantUid")
    Optional<PaymentIntent> findForUpdate(@Param("merchantUid") String merchantUid);
}
