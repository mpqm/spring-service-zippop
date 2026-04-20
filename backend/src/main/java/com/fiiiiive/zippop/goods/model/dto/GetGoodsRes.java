package com.fiiiiive.zippop.goods.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 굿즈 조회 응답 DTO
@Getter
@Builder
public class GetGoodsRes {
    private String popupName;
    private Long goodsIdx;
    private String goodsName;
    private Integer goodsPrice;
    private String goodsContent;
    private Integer goodsAmount;
    private String goodsStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetGoodsImageRes> getGoodsImageResList;
}