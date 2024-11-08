package com.fiiiiive.zippop.review.repository;


import com.fiiiiive.zippop.review.model.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> { }
