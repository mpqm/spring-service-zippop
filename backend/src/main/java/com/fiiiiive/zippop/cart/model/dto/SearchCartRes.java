package com.fiiiiive.zippop.cart.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCartRes {
    private Long cartIdx;
    private List<SearchCartItemRes> searchCartItemResList;
}
