package com.fiiiiive.zippop.goods.entity;

import com.fiiiiive.zippop.cart.entity.CartItem;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.goods.dto.GoodsDto;
import com.fiiiiive.zippop.orders.entity.OrdersDetail;
import com.fiiiiive.zippop.store.entity.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.domain.Page;

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

    // 굿즈 이름(필수 입력, 최대 1000자 제한)
    @Column(nullable = false, length = 1000)
    private String name;

    // 굿즈 설명(최대 1000자 제한)
    @Column(length = 1000)
    private String content;

    // 굿즈 상태(필수 입력)
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    // 수량 (필수 입력)
    @Setter
    @Column(nullable = false)
    @Positive(message = "수량은 양수여야 합니다.")
    @Max(value = 10000, message = "수량은 최대 10000개 이하여야 합니다.")
    private Integer amount;

    // 가격 (필수 입력)
    @Column(nullable = false)
    @Positive(message = "가격은 양수여야 합니다.")
    @Min(value = 1, message = "가격은 최소 1원 이상이어야 합니다.")
    private Integer price;


    // Update
    public Goods update(GoodsDto.UpdateGoodsReq dto){
        this.name = dto.getGoodsName();
        this.content = dto.getGoodsContent();
        this.price = dto.getGoodsPrice();
        this.amount = dto.getGoodsAmount();
        return this;
    }

    // ToDto
    public GoodsDto.SearchGoodsRes toDto() {
        return GoodsDto.SearchGoodsRes.builder()
                .storeName(this.getStore().getName())
                .goodsIdx(this.getIdx())
                .goodsName(this.getName())
                .goodsPrice(this.getPrice())
                .goodsContent(this.getContent())
                .goodsAmount(this.getAmount())
                .goodsStatus(this.getStatus().name())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .searchGoodsImageResList(GoodsImage.toDtoList(this.getGoodsImageList()))
                .build();
    }

    public static Page<GoodsDto.SearchGoodsRes> toDtoPage(Page<Goods> goodsPage) {
        return goodsPage.map(Goods::toDto);
    }

    // OneToMany
    @OneToMany(mappedBy = "goods")
    private List<CartItem> cartItemList;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsImage> goodsImageList;

    @OneToMany(mappedBy = "goods")
    private List<OrdersDetail> ordersDetailList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx")
    private Store store;
}
