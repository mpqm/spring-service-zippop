package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.popup_goods.model.dto.GetPopupGoodsRes;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCartRes {
    private Long cartIdx;
    private Integer itemCount;
    private Integer itemPrice;
    private GetPopupGoodsRes getPopupGoodsRes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
