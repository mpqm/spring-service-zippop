package com.fiiiiive.zippop.popup.model.entity;

import com.fiiiiive.zippop.popup.model.dto.GetPopupImageRes;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupImage {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String url;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="popup_idx")
    private Popup popup;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // ToDto
    public GetPopupImageRes toDto() {
        return GetPopupImageRes.builder()
                .popupImageIdx(this.getIdx())
                .popupImageUrl(this.getUrl())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static List<GetPopupImageRes> toDtoList(List<PopupImage> popupImageList) {
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
