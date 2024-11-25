package com.fiiiiive.zippop.goods.model.entity;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Goods extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String name;
    private String content;
    private Integer price;
    private String status;

    // Setter
    @Setter
    private Integer amount;

    // Update
    public Goods update(String name, String content, Integer price, Integer amount){
        this.name = name;
        this.content = content;
        this.price = price;
        this.amount = amount;
        return this;
    }

    // OneToMany
    @OneToMany(mappedBy = "goods")
    private List<CartItem> cartItemList;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsImage> goodsImageList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx")
    private Store store;
}
