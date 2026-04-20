package com.fiiiiive.zippop.goods.model.entity;

import com.fiiiiive.zippop.cart.model.entity.CartItem;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.enums.GoodsStatus;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import com.fiiiiive.zippop.goods.model.dto.UpdateGoodsReq;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.domain.Page;

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

    // 굿즈 이름(필수 입력, 최대 1000자 제한)
    @Column(nullable = false, length = 1000)
    private String name;

    // 굿즈 설명(최대 1000자 제한)
    @Column(length = 1000)
    private String content;

    // 굿즈 상태(필수 입력)
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GoodsStatus status;

    // 수량 (필수 입력)
    @Column(nullable = false)
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer amount;

    // 가격 (필수 입력)
    @Column(nullable = false)
    @Positive(message = "가격은 양수여야 합니다.")
    @Min(value = 1, message = "가격은 최소 1원 이상이어야 합니다.")
    private Integer price;

    // OneToMany
    @OneToMany(mappedBy = "goods")
    private List<CartItem> cartItemList;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsImage> goodsImageList = new ArrayList<>();

    @OneToMany(mappedBy = "goods")
    private List<OrdersDetail> ordersDetailList = new ArrayList<>();

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx")
    private Popup popup;

    // Create
    public static Goods create(String name, Integer amount, Integer price, String content, Popup popup) {
        return Goods.builder()
                .name(name)
                .amount(amount)
                .price(price)
                .content(content)
                .popup(popup)
                .status(GoodsStatus.GOODS_RESERVED)
                .build();
    }

    // Update
    public Goods update(UpdateGoodsReq req){
        this.name = req.getGoodsName();
        this.content = req.getGoodsContent();
        this.price = req.getGoodsPrice();
        this.amount = req.getGoodsAmount();
        return this;
    }

    // ToDto
    public GetGoodsRes toDto() {
        return GetGoodsRes.builder()
                .popupName(this.getPopup().getName())
                .goodsIdx(this.getIdx())
                .goodsName(this.getName())
                .goodsPrice(this.getPrice())
                .goodsContent(this.getContent())
                .goodsAmount(this.getAmount())
                .goodsStatus(this.getStatus().name())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .getGoodsImageResList(GoodsImage.toDtoList(this.getGoodsImageList()))
                .build();
    }

    public static Page<GetGoodsRes> toDtoPage(Page<Goods> goodsPage) {
        return goodsPage.map(Goods::toDto);
    }

    // 이미지 추가
    public void addImages(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return; // 이미지 없어도 됨
        }
        urls.forEach(this::addImage);
    }

    public void addImage(String url) {
        GoodsImage image = GoodsImage.create(this, url);
        this.goodsImageList.add(image);
    }

    public void replaceImages(List<String> urls) {
        this.goodsImageList.clear();   // orphanRemoval로 기존 이미지 삭제
        addImages(urls);              // 새 이미지 추가
    }

    public void updateAmount(Integer amount) {
        this.amount = amount;
    }

    public void changeToStock() {
        this.status = GoodsStatus.GOODS_STOCK;
    }

}
