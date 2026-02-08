package com.fiiiiive.zippop.popup.repository;

import com.fiiiiive.zippop.popup.model.PopupLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PopupLikeRepository extends JpaRepository<PopupLike, Long> {

    // 고객, 팝업 인덱스로 조회
    @Query("SELECT pl FROM PopupLike pl " +
            "JOIN FETCH pl.customer plc " +
            "JOIN FETCH pl.popup plp " +
            "WHERE plc.idx = :customerIdx AND plp.idx = :popupIdx")
    Optional<PopupLike> findByCustomerIdxAndPopupIdx(@Param("customerIdx") Long customerIdx, @Param("popupIdx") Long popupIdx);

    // 고객 인덱스로 페이징 조회
    @Query("SELECT pl FROM PopupLike pl " +
            "JOIN FETCH pl.customer plc " +
            "WHERE plc.idx = :customerIdx")
    Page<PopupLike> findAllByCustomerIdx(@Param("customerIdx") Long customerIdx, Pageable pageable);

    // 고객, 팝업 인덱스로 삭제
    @Modifying
    @Query("DELETE FROM PopupLike pl " +
            "WHERE pl.customer.idx = :customerIdx AND pl.popup.idx = :popupIdx")
    void deleteByCustomerIdxAndPopupIdx(@Param("customerIdx") Long customerIdx, @Param("popupIdx") Long popupIdx);

}
