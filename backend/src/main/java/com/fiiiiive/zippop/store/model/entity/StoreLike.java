package com.fiiiiive.zippop.store.model.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.store.model.dto.StoreDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreLike extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_idx")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_idx")
    private Customer customer;

    // ToDTO
    public StoreDto.SearchStoreLikeRes toDto() {
        return StoreDto.SearchStoreLikeRes.builder()
                .storeIdx(this.getStore().getIdx())
                .companyEmail(this.getStore().getCompanyEmail())
                .storeName(this.getStore().getName())
                .storeContent(this.getStore().getContent())
                .storeAddress(this.getStore().getAddress())
                .category(this.getStore().getCategory())
                .likeCount(this.getStore().getLikeCount())
                .totalPeople(this.getStore().getTotalPeople())
                .storeStartDate(this.getStore().getStartDate())
                .storeEndDate(this.getStore().getEndDate())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .searchStoreImageResList(StoreImage.toDtoList(this.getStore().getStoreImageList()))
                .build();
    }

    public static Page<StoreDto.SearchStoreLikeRes> toDtoPage(Page<StoreLike> storeLikePage) {
        return storeLikePage.map(StoreLike::toDto);
    }

}

