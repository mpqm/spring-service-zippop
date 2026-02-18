package com.fiiiiive.zippop.payout.repository;

import com.fiiiiive.zippop.payout.model.Payout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {

    // 정산 내역 조회
    @Query("SELECT p FROM Payout p WHERE p.popup.idx = :popupIdx")
    Page<Payout> findAllByPopupIdx(Long popupIdx, Pageable pageable);

    // 중복 정산 방지 (Payout.popup.idx 기준)
    @Query("SELECT COUNT(p) > 0 FROM Payout p WHERE p.popup.idx = :popupIdx AND p.payoutDate = :payoutDate")
    boolean existsByPopupIdxAndPayoutDate(@Param("popupIdx") Long popupIdx, @Param("payoutDate") LocalDate payoutDate);

}