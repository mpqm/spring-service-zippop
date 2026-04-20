package com.fiiiiive.zippop.popup.repository;

import com.fiiiiive.zippop.popup.model.entity.PopupReview;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PopupReviewRepository extends JpaRepository<PopupReview, Long> {

    // 리뷰 인덱스로 조회
    @Query("SELECT pr From PopupReview pr " +
            "JOIN FETCH pr.customer prc JOIN FETCH pr.popup prp " +
            "WHERE prc.idx = :customerIdx AND prp.idx = :popupIdx")
    Optional<PopupReview> findByPopupIdxAndCustomerIdx(@Param("popupIdx") Long popupIdx, @Param("customerIdx") Long customerIdx);

    // 팝업 인덱스로 목록 조회
    @Query("SELECT pr FROM PopupReview pr " +
            "JOIN FETCH pr.popup prp " +
            "WHERE prp.idx = :popupIdx")
    Page<PopupReview> findAllByPopupIdx(@Param("popupIdx") Long popupIdx, Pageable pageable);

    // 고객의 인덱스로 목록 조회
    @Query("SELECT pr FROM PopupReview pr " +
            "JOIN FETCH pr.customer prc " +
            "WHERE prc.idx = :customerIdx")
    Page<PopupReview> findAllByCustomerIdx(@Param("customerIdx") Long customerIdx, Pageable pageable);

}
