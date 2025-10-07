package com.fiiiiive.zippop.store.entity;

import com.fiiiiive.zippop.cart.entity.Cart;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.goods.entity.Goods;
import com.fiiiiive.zippop.auth.entity.Company;
import com.fiiiiive.zippop.payout.entity.Payout;
import com.fiiiiive.zippop.reserve.entity.Reserve;
import com.fiiiiive.zippop.store.dto.StoreDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Store extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 회사 이메일 (필수, 최대 100자)
    @Column(nullable = false, length = 100)
    private String companyEmail;

    // 스토어 이름 (필수, 최대 100자)
    @Column(nullable = false, length = 100)
    private String name;

    // 스토어 주소 (필수, 최대 200자)
    @Column(nullable = false, length = 200)
    private String address;

    // 스토어 설명 (필수, 최대 500자)
    @Column(nullable = false, length = 500)
    private String content;

    // 스토어 시작 날짜 (필수, 문자열로 저장)
    @Column(nullable = false, length = 10)
    private String startDate;

    // 스토어 종료 날짜 (필수, 문자열로 저장)
    @Column(nullable = false, length = 10)
    private String endDate;

    // 카테고리 (필수, 최대 50자)
    @Column(nullable = false, length = 50)
    private String category;

    // 좋아요 수 (0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "좋아요 수는 0 이상이어야 합니다.")
    private Integer likeCount;

    // Setter
    @Setter
    @Column(nullable = false)
    @Positive(message = "총 인원수는 1명 이상이어야 합니다.")
    private Integer totalPeople;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    // OneToMany
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreReview> storeReviewList;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goods> goodsList;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<StoreImage> storeImageList;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserve> reserveList;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> cartList;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreLike> storeLikeList;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payout> payoutList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_idx")
    private Company company;

    // Update
    public Store update(StoreDto.UpdateStoreReq dto){
        this.name = dto.getStoreName();
        this.content = dto.getStoreContent();
        this.address = dto.getStoreAddress();
        this.category = dto.getCategory();
        this.totalPeople = dto.getTotalPeople();
        this.startDate = dto.getStoreStartDate();
        this.endDate = dto.getStoreEndDate();
        return this;
    }

    // ToDto
    public StoreDto.SearchStoreRes toDto() {
        return StoreDto.SearchStoreRes.builder()
                .storeIdx(this.getIdx())
                .companyEmail(this.getCompanyEmail())
                .storeName(this.getName())
                .storeContent(this.getContent())
                .storeAddress(this.getAddress())
                .category(this.getCategory())
                .likeCount(this.getLikeCount())
                .totalPeople(this.getTotalPeople())
                .storeStatus(this.getStatus().name())
                .storeStartDate(this.getStartDate())
                .storeEndDate(this.getEndDate())
                .searchStoreImageResList(StoreImage.toDtoList(this.getStoreImageList()))
                .build();
    }

    public static Page<StoreDto.SearchStoreRes> toDtoPage(Page<Store> storePage) {
        return storePage.map(Store::toDto);
    }
}

