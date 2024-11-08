package com.fiiiiive.zippop.comment.repository;

import com.fiiiiive.zippop.comment.model.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("SELECT cl FROM CommentLike cl WHERE cl.customer.idx = :customerIdx and cl.comment.idx = :commentIdx")
    Optional<CommentLike> findByCustomerIdxAndCommentIdx(Long customerIdx, Long commentIdx);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.customer.idx = :customerIdx AND cl.comment.idx = :commentIdx")
    void deleteByCustomerIdxAndCommentIdx(Long customerIdx, Long commentIdx);
}