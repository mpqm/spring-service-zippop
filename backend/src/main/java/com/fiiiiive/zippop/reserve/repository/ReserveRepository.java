package com.fiiiiive.zippop.reserve.repository;

import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    @Query("SELECT r FROM Reserve r WHERE DATE(r.startDate) = :startDate")
    List<Reserve> findAllByStartDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.store rs " +
            "WHERE rs.idx = :storeIdx AND rs.companyEmail = :companyEmail")
    Optional<Page<Reserve>> findAllByCompanyEmail(@Param("storeIdx") Long storeIdx, @Param("companyEmail") String companyEmail, Pageable pageable);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.store rs " +
            "WHERE rs.status = :status")
    Page<Reserve> findAllByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.store rs " +
            "WHERE rs.status = :status " +
             "AND (rs.address LIKE %:keyword% OR rs.name LIKE %:keyword% OR rs.category LIKE %:keyword% OR rs.startDate LIKE %:keyword% OR rs.companyEmail LIKE %:keyword%)")
    Page<Reserve> findAllByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);
}