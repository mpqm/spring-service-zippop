package com.fiiiiive.zippop.domain.cart.entity;

import com.fiiiiive.zippop.domain.cart.dto.CartDto;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.domain.auth.entity.Customer;
import com.fiiiiive.zippop.domain.store.entity.Store;
import com.fiiiiive.zippop.domain.store.entity.StoreImage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart extends BaseEntity {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // OneToMany
    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItemList = new ArrayList<>();

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx")
    private Store store;

    // toDTO
    public CartDto.SearchCartRes toDto() {
        return CartDto.SearchCartRes.builder()
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
                .storeStatus(this.getStore().getStatus().name())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .searchStoreImageResList(StoreImage.toDtoList(this.getStore().getStoreImageList()))
                .build();
    }

    public static Page<CartDto.SearchCartRes> toDtoPage(Page<Cart> cartPage) {
        return cartPage.map(Cart::toDto);
    }
}