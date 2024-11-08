package com.fiiiiive.zippop.popup_store.model.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.favorite.model.entity.Favorite;
import com.fiiiiive.zippop.member.model.entity.Company;
import com.fiiiiive.zippop.popup_goods.model.entity.PopupGoods;
import com.fiiiiive.zippop.popup_reserve.model.entity.PopupReserve;
import com.fiiiiive.zippop.popup_review.model.entity.PopupReview;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupStore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeIdx;
    private String companyEmail;
    @Column(unique = true)
    private String storeName;
    private String storeAddress;
    private String storeContent;
    private String storeStartDate;
    private String storeEndDate;
    private String category;
    private Integer totalPeople;
    private Integer likeCount;

    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PopupReview> reviewList = new ArrayList<>();
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PopupGoods> popupGoodsList = new ArrayList<>();
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favoriteList;
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<PopupStoreImage> popupstoreImageList;
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupReserve> popupReserveList = new ArrayList<>();
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupStoreLike> popupStoreLikeList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyIdx")
    @JsonBackReference
    private Company company;
}

