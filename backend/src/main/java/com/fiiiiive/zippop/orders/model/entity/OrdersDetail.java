package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.dto.GoodsDto;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import com.fiiiiive.zippop.orders.model.dto.OrdersDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrdersDetail extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 각 상품의 가격 (필수, 0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "각 상품의 가격은 0 이상이어야 합니다.")
    private Integer eachPrice;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_idx")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_idx")
    private Goods goods ;

    public OrdersDto.SearchOrdersDetailRes toDto() {
        return OrdersDto.SearchOrdersDetailRes.builder()
                .ordersDetailIdx(this.getIdx())
                .eachPrice(this.getEachPrice())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .searchGoodsRes(goods.toDto())
                .build();
    }

    public static List<OrdersDto.SearchOrdersDetailRes> toDtoList(List<OrdersDetail> ordersDetailList) {
        return ordersDetailList.stream()
                .map(OrdersDetail::toDto)
                .collect(Collectors.toList());
    }

}
