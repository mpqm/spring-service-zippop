package com.fiiiiive.zippop.reserve.repository;

import com.fiiiiive.zippop.global.enums.PopupStatus;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    @Query("SELECT r FROM Reserve r WHERE DATE(r.startDate) = :startDate")
    List<Reserve> findAllByStartDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.popup rp " +
            "WHERE rp.idx = :popupIdx AND rp.companyEmail = :companyEmail")
    Optional<Page<Reserve>> findAllByCompanyEmail(@Param("popupIdx") Long popupIdx, @Param("companyEmail") String companyEmail, Pageable pageable);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.popup rp " +
            "WHERE rp.status = :status")
    Page<Reserve> findAllByStatus(@Param("status") PopupStatus status, Pageable pageable);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.popup rp " +
            "WHERE rp.status = :status " +
             "AND (rp.address LIKE CONCAT('%', :keyword, '%') OR rp.name LIKE CONCAT('%', :keyword, '%') OR rp.category LIKE CONCAT('%', :keyword, '%') OR rp.companyEmail LIKE CONCAT('%', :keyword, '%'))")
    Page<Reserve> findAllByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") PopupStatus status, Pageable pageable);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.popup rp " +
            "WHERE rp.idx = :popupIdx AND rp.status = :status")
    Page<Reserve> findAllByPopupIdx(@Param("popupIdx") Long popupIdx, @Param("status") PopupStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Reserve r SET r.totalPeople = r.totalPeople - :decreasePeople WHERE r.idx = :reserveIdx")
    void decreaseTotalPeople(@Param("reserveIdx") Long reserveIdx, @Param("decreasePeople") Integer decreasePeople);

}