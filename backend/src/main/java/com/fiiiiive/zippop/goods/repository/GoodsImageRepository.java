package com.fiiiiive.zippop.goods.repository;

import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> { }
