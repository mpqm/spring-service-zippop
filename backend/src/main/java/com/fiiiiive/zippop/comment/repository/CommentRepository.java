package com.fiiiiive.zippop.comment.repository;

import com.fiiiiive.zippop.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Page<Comment>> findByPostIdx(Long postIdx, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.customer.idx = :customerIdx")
    Optional<Page<Comment>> findByCustomerIdx(Long customerIdx, Pageable pageable); // Pageable 추가

    @Query("SELECT c FROM Comment c WHERE c.idx = :commentIdx")
    Optional<Comment> findByCommentIdx(Long commentIdx);
}
