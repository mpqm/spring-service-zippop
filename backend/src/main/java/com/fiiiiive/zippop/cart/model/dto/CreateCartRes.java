package com.fiiiiive.zippop.cart.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartRes {
    private Long cartIdx;
    private Long cartItemIdx;
}
