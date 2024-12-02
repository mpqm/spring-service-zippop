package com.fiiiiive.zippop.reserve.repository;

import com.fiiiiive.zippop.reserve.model.entity.Reserve;
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
    List<Reserve> findAllByStartDate(LocalDate startDate);

    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.store rs " +
            "WHERE rs.companyEmail = :companyEmail")
    Optional<Page<Reserve>> findAllByCompanyEmail(String companyEmail, Pageable pageable);
}