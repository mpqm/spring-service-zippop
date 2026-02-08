package com.fiiiiive.zippop.popup.model;

import com.fiiiiive.zippop.account.model.Company;
import com.fiiiiive.zippop.cart.model.Cart;
import com.fiiiiive.zippop.global.enums.StoreStatus;
import com.fiiiiive.zippop.goods.model.Goods;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.goods.model.GoodsImage;
import com.fiiiiive.zippop.payout.model.Payout;
import com.fiiiiive.zippop.reserve.model.Reserve;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Popup extends BaseEntity {
    // id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 회사 이메일 (필수, 최대 100자)
    @Column(nullable = false, length = 100)
    private String companyEmail;

    // 팝업 이름 (필수, 최대 100자)
    @Column(nullable = false, length = 100)
    private String name;

    // 팝업 주소 (필수, 최대 200자)
    @Column(nullable = false, length = 200)
    private String address;

    // 팝업 설명 (필수, 최대 500자)
    @Column(nullable = false, length = 500)
    private String content;

    // 팝업 시작 날짜 (필수, 문자열로 저장)
    @Column(nullable = false, length = 10)
    private LocalDate startDate;

    // 팝업 종료 날짜 (필수, 문자열로 저장)
    @Column(nullable = false, length = 10)
    private LocalDate endDate;

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
    private StoreStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // OneToMany
    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupReview> popupReviewList;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goods> goodsList;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupImage> popupImageList = new ArrayList<>();

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserve> reserveList;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> cartList;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupLike> popupLikeList;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payout> payoutList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_idx")
    private Company company;

    public static Popup create(
            String companyEmail,
            String name,
            String content,
            String address,
            String category,
            Integer totalPeople,
            LocalDate startDate,
            LocalDate endDate,
            Company company
    ) {
        return Popup.builder()
                .companyEmail(companyEmail)
                .name(name)
                .content(content)
                .address(address)
                .category(category)
                .totalPeople(totalPeople)
                .startDate(startDate)
                .endDate(endDate)
                .likeCount(0)
                .status(StoreStatus.STORE_START)
                .company(company)
                .build();
    }


    // Update
    public Popup update(PopupDto.UpdatePopupReq dto){
        this.name = dto.getPopupName();
        this.content = dto.getPopupContent();
        this.address = dto.getPopupAddress();
        this.category = dto.getCategory();
        this.totalPeople = dto.getTotalPeople();
        this.startDate = dto.getPopupStartDate();
        this.endDate = dto.getPopupEndDate();
        return this;
    }

    // ToDto
    public PopupDto.SearchPopupRes toDto() {
        return PopupDto.SearchPopupRes.builder()
                .popupIdx(this.getIdx())
                .companyEmail(this.getCompanyEmail())
                .popupName(this.getName())
                .popupContent(this.getContent())
                .popupAddress(this.getAddress())
                .category(this.getCategory())
                .likeCount(this.getLikeCount())
                .totalPeople(this.getTotalPeople())
                .popupStatus(this.getStatus().name())
                .popupStartDate(this.getStartDate())
                .popupEndDate(this.getEndDate())
                .searchPopupImageResList(PopupImage.toDtoList(this.getPopupImageList()))
                .build();
    }

    public static Page<PopupDto.SearchPopupRes> toDtoPage(Page<Popup> popupPage) {
        return popupPage.map(Popup::toDto);
    }

    public void endPopup() {
        this.status = StoreStatus.STORE_END;

        for (Goods goods : this.goodsList) {
            goods.changeToStock();
        }
    }

    // 이미지 추가
    public void addImages(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return; // 이미지 없어도 됨
        }
        urls.forEach(this::addImage);
    }

    public void addImage(String url) {
        PopupImage image = PopupImage.create(this, url);
        this.popupImageList.add(image);
    }

    public void replaceImages(List<String> urls) {
        this.popupImageList.clear();   // orphanRemoval로 기존 이미지 삭제
        addImages(urls);              // 새 이미지 추가
    }

    // 좋아요 증감
    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        this.likeCount--;
    }


}
