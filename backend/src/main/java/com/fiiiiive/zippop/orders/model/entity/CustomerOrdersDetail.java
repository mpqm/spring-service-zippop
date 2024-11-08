package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.entity.PopupGoods;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrdersDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerOrderDetailIdx;
    private Integer eachPrice;
    private String trackingNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerOrdersIdx")
    private CustomerOrders customerOrders;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popupGoodsIdx")
    private PopupGoods popupGoods ;
}
