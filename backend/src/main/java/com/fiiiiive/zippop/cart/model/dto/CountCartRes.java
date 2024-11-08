package com.fiiiiive.zippop.cart.model.dto;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountCartRes {
    private Cart cart;
}
