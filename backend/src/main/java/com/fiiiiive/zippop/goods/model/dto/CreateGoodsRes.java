package com.fiiiiive.zippop.goods.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGoodsRes {
    private Long goodsIdx;
    private String goodsName;
    private Integer goodsPrice;
    private Integer goodsAmount;
    private String goodsContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
}
