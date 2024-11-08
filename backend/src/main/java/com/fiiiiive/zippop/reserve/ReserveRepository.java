package com.fiiiiive.zippop.reserve;

import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface ReserveRepository extends JpaRepository<Reserve, Long> { }