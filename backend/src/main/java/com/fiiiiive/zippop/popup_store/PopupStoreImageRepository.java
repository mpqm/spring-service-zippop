package com.fiiiiive.zippop.popup_store;

import com.fiiiiive.zippop.popup_store.model.entity.PopupStoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupStoreImageRepository extends JpaRepository<PopupStoreImage, Long> { }
