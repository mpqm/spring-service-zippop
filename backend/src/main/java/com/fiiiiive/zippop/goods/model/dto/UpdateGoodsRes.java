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
public class UpdateGoodsRes {
    private Long productIdx;
    private String productName;
    private Integer productPrice;
    private Integer productAmount;
    private String productContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetGoodsImageRes> popupGoodsImageList = new ArrayList<>();;
}
