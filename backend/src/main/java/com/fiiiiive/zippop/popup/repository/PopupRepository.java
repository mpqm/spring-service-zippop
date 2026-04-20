package com.fiiiiive.zippop.popup.repository;

import com.fiiiiive.zippop.global.enums.PopupStatus;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.*;

public interface PopupRepository extends JpaRepository<Popup, Long> {

    // 팝업 인덱스로 조회
    @Query("SELECT p FROM Popup p " +
            "WHERE p.idx = :popupIdx")
    Optional<Popup> findByPopupIdx(@Param("popupIdx") Long popupIdx);

    // 팝업 인덱스 및 기업 이메일로 조회
    @Query("SELECT p FROM Popup p " +
            "WHERE p.idx = :popupIdx AND p.companyEmail = :companyEmail")
    Optional<Popup> findByPopupIdxAndCompanyEmail(@Param("popupIdx") Long popupIdx, @Param("companyEmail") String companyEmail);

    // 기업 이메일로 목록 조회
    @Query("SELECT p FROM Popup p " +
            "WHERE p.companyEmail = :companyEmail")
    Page<Popup> findAllByCompanyEmail(@Param("companyEmail") String companyEmail, Pageable pageable);

    // 상태값으로 목록 조회
    @Query("SELECT p FROM Popup p WHERE p.status = :status")
    Page<Popup> findAllByStatus(@Param("status") String status, Pageable pageable);

    // 검색어, 상태 기반으로 목록 조회
    @Query("SELECT p FROM Popup p " +
            "WHERE p.status = :status " +
            "AND (p.address LIKE %:keyword% OR p.name LIKE %:keyword% OR p.category LIKE %:keyword% OR p.companyEmail LIKE %:keyword%)")
    Page<Popup> findAllByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") PopupStatus status, Pageable pageable);

    // 검색어, 기업 이메일로 목록 조회
    @Query("SELECT p FROM Popup p " +
            "WHERE p.companyEmail = :companyEmail " +
            "AND (p.address LIKE %:keyword% OR p.name LIKE %:keyword% OR p.category LIKE %:keyword%)")
    Page<Popup> findAllByKeywordAndCompanyEmail(@Param("keyword") String keyword, @Param("companyEmail") String companyEmail, Pageable pageable);

    // 팝업 종료일 조회
    @Query("SELECT p FROM Popup p WHERE DATE(p.endDate) = :endDate")
    List<Popup> findAllByEndDate(@Param("endDate") LocalDate endDate);

}
