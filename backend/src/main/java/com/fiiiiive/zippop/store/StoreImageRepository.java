package com.fiiiiive.zippop.store;

import com.fiiiiive.zippop.store.model.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreImageRepository extends JpaRepository<StoreImage, Long> { }
