package com.fiiiiive.zippop.cart.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer count;
    private Integer price;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_idx")
    private Goods goods;
}
