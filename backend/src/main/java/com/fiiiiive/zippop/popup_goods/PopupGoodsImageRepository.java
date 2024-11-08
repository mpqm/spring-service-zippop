package com.fiiiiive.zippop.popup_goods;

import com.fiiiiive.zippop.popup_goods.model.entity.PopupGoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupGoodsImageRepository extends JpaRepository<PopupGoodsImage, Long> { }
