package com.fiiiiive.zippop.cart.model.entity;

import com.fiiiiive.zippop.cart.model.dto.CartDto;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem extends BaseEntity {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    // 상품 수량 (필수, 최소 1)
    @Column(nullable = false)
    @Min(value = 1, message = "상품 수량은 최소 1개 이상이어야 합니다.")
    private Integer count;

    // 총 가격 (필수, 0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "총 가격은 0 이상이어야 합니다.")
    private Integer price;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_idx")
    private Goods goods;

    // toDto
    public CartDto.SearchCartItemRes toDto() {
        return CartDto.SearchCartItemRes.builder()
                .cartItemIdx(this.getIdx())
                .count(this.getCount())
                .price(this.getPrice())
                .searchGoodsRes(this.getGoods().toDto())
                .build();
    }

    public static List<CartDto.SearchCartItemRes> toDtoList(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(CartItem::toDto)
                .collect(Collectors.toList());
    }

}
