package com.fiiiiive.zippop.store.model.entity;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.auth.model.entity.Company;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import jakarta.persistence.*;
import lombok.*;

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
    private String companyEmail;
    private String name;
    private String address;
    private String content;
    private String startDate;
    private String endDate;
    private String category;
    private Integer likeCount;

    // Setter
    @Setter
    private Integer totalPeople;
    @Setter
    private String status;

    // Update
    public Store update(String name, String content, String address, String category, Integer totalPeople, String startDate, String endDate){
        this.name = name;
        this.content = content;
        this.address = address;
        this.category = category;
        this.totalPeople = totalPeople;
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

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

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_idx")
    private Company company;
}

