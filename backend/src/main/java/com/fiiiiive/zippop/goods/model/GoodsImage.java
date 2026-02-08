package com.fiiiiive.zippop.goods.model;

import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GoodsImage extends BaseEntity {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String url;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="goods_idx")
    private Goods goods;

    // ToDto
    public GoodsDto.SearchGoodsImageRes toDto() {
        return GoodsDto.SearchGoodsImageRes.builder()
                .goodsImageIdx(this.getIdx())
                .goodsImageUrl(this.getUrl())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static List<GoodsDto.SearchGoodsImageRes> toDtoList(List<GoodsImage> goodsImageList) {
        return goodsImageList.stream()
                .map(GoodsImage::toDto)
                .collect(Collectors.toList());
    }

    public static GoodsImage create(Goods goods, String url) {
        GoodsImage image = new GoodsImage();
        image.url = url;
        image.goods = goods;
        return image;
    }
}
