package com.fiiiiive.zippop.goods.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchGoodsImageRes {
    private Long goodsImageIdx;
    private String goodsImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
