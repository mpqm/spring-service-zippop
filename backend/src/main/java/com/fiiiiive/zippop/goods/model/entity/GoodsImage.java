package com.fiiiiive.zippop.goods.model.entity;

import com.fiiiiive.zippop.goods.model.dto.GetGoodsImageRes;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GoodsImage {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String url;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="goods_idx")
    private Goods goods;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // ToDto
    public GetGoodsImageRes toDto() {
        return GetGoodsImageRes.builder()
                .goodsImageIdx(this.getIdx())
                .goodsImageUrl(this.getUrl())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static List<GetGoodsImageRes> toDtoList(List<GoodsImage> goodsImageList) {
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
