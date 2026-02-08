package com.fiiiiive.zippop.popup.model;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.Goods;
import com.fiiiiive.zippop.goods.model.GoodsImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupImage extends BaseEntity {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String url;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="popup_idx")
    private Popup popup;

    // ToDto
    public PopupDto.SearchPopupImageRes toDto() {
        return PopupDto.SearchPopupImageRes.builder()
                .popupImageIdx(this.getIdx())
                .popupImageUrl(this.getUrl())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static List<PopupDto.SearchPopupImageRes> toDtoList(List<PopupImage> popupImageList) {
        return popupImageList.stream()
                .map(PopupImage::toDto)
                .collect(Collectors.toList());
    }

    public static PopupImage create(Popup popup, String url) {
        PopupImage image = new PopupImage();
        image.url = url;
        image.popup = popup;
        return image;
    }
}
