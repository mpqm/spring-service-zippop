package com.fiiiiive.zippop.cart.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountCartItemRes {
    private Long cartItemIdx;
    private Integer count;
}
