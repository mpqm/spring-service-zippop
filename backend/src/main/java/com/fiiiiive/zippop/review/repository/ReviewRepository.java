package com.fiiiiive.zippop.review.repository;

import com.fiiiiive.zippop.review.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 팝업 스토어 인덱스를 기반으로 스토어의 리뷰를 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.store s WHERE s.storeIdx = :storeIdx")
    Optional<Page<Review>> findByStoreIdx(Long storeIdx, Pageable pageable);

    // 고객의 인덱스를 기반으로 자신이 쓴 리뷰를 전체 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.customer c WHERE c.idx = :customerIdx")
    Optional<Page<Review>> findByCustomerIdx(Long customerIdx, Pageable pageable);
}
