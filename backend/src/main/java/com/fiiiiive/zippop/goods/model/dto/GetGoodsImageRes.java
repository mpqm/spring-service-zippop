package com.fiiiiive.zippop.goods.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 굿즈 이미지 조회 응답 DTO
@Getter
@Builder
public class GetGoodsImageRes {
    private Long goodsImageIdx;
    private String goodsImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
