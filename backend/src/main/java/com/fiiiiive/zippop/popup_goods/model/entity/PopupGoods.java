package com.fiiiiive.zippop.popup_goods.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.popup_store.model.entity.PopupStore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupGoods extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productIdx;
    private String productName;
    private Integer productPrice;
    private String productContent;
    private Integer productAmount;


    @OneToMany(mappedBy = "popupGoods")
    private List<Cart> carts;
    @OneToMany(mappedBy = "popupGoods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupGoodsImage> popupGoodsImageList = new ArrayList<>();;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeIdx")
    @JsonBackReference
    private PopupStore popupStore;
}
