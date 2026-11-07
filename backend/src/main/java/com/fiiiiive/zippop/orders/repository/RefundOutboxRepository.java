package com.fiiiiive.zippop.orders.repository;

import com.fiiiiive.zippop.orders.model.entity.RefundOutbox;
import com.fiiiiive.zippop.orders.model.entity.RefundOutboxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefundOutboxRepository extends JpaRepository<RefundOutbox, Long> {
    Optional<RefundOutbox> findByImpUid(String impUid);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RefundOutbox r where r.idx = :idx")
    Optional<RefundOutbox> findForUpdate(@Param("idx") Long idx);
    List<RefundOutbox> findTop100ByStatusInAndNextAttemptAtLessThanEqualOrderByNextAttemptAt(
            List<RefundOutboxStatus> statuses, LocalDateTime now);
}
