package com.fiiiiive.zippop.cart.model;

import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.goods.model.Goods;
import com.fiiiiive.zippop.popup.model.Popup;
import com.fiiiiive.zippop.popup.model.PopupImage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // OneToMany
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItemList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx")
    private Popup popup;

    // toDTO
    public CartDto.GetCartRes toDto() {
        return CartDto.GetCartRes.builder()
                .popupIdx(this.getPopup().getIdx())
                .companyEmail(this.getPopup().getCompanyEmail())
                .popupName(this.getPopup().getName())
                .popupContent(this.getPopup().getContent())
                .popupAddress(this.getPopup().getAddress())
                .category(this.getPopup().getCategory())
                .likeCount(this.getPopup().getLikeCount())
                .totalPeople(this.getPopup().getTotalPeople())
                .popupStartDate(this.getPopup().getStartDate())
                .popupEndDate(this.getPopup().getEndDate())
                .popupStatus(this.getPopup().getStatus().name())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .searchPopupImageResList(PopupImage.toDtoList(this.getPopup().getPopupImageList()))
                .build();
    }

    public static Page<CartDto.GetCartRes> toDtoPage(Page<Cart> cartPage) {
        return cartPage.map(Cart::toDto);
    }


    public void validateNoDuplicateGoods(Goods goods) {
        boolean exists = this.cartItemList.stream()
                .anyMatch(item -> item.getGoods().equals(goods));

        if (exists) {
            throw new BaseException(BaseMessage.CART_REGISTER_FAIL_ITEM_EXIST);
        }
    }

}