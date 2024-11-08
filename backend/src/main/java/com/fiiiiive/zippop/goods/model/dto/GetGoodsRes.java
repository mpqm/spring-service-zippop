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
public class GetGoodsRes {
    private Long goodsIdx;
    private String goodsName;
    private Integer goodsPrice;
    private String goodsContent;
    private Integer goodsAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();;
}
