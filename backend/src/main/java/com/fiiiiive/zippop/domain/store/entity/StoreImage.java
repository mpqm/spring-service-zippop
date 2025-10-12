package com.fiiiiive.zippop.domain.store.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.domain.store.dto.StoreDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreImage extends BaseEntity {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String url;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_idx")
    private Store store;

    // ToDto
    public StoreDto.SearchStoreImageRes toDto() {
        return StoreDto.SearchStoreImageRes.builder()
                .storeImageIdx(this.getIdx())
                .storeImageUrl(this.getUrl())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static List<StoreDto.SearchStoreImageRes> toDtoList(List<StoreImage> storeImageList) {
        return storeImageList.stream()
                .map(StoreImage::toDto)
                .collect(Collectors.toList());
    }
}
