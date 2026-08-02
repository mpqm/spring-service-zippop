package com.fiiiiive.zippop.cart.model.entity;

import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.cart.model.dto.GetCartRes;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.entity.PopupImage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.ArrayList;

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
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItemList = new ArrayList<>();

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx")
    private Popup popup;

    public static Cart create(
            Long customerIdx,
            Popup popup
    ) {
        Customer customerRef = Customer.builder()
                .idx(customerIdx)
                .build();
        return Cart.builder()
                .customer(customerRef)
                .popup(popup)
                .build();
    }

    // toDTO
    public GetCartRes toDto() {
        return GetCartRes.builder()
                .cartIdx(this.getIdx())
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
                .getPopupImageResList(PopupImage.toDtoList(this.getPopup().getPopupImageList()))
                .build();
    }

    public static Page<GetCartRes> toDtoPage(Page<Cart> cartPage) {
        return cartPage.map(Cart::toDto);
    }


    public void validateNoDuplicateGoods(Goods goods) {
        if (this.cartItemList == null) {
            this.cartItemList = new ArrayList<>();
        }
        boolean exists = this.cartItemList.stream()
                .anyMatch(item -> item.getGoods().equals(goods));

        if (exists) {
            throw new ServiceException(ServiceErrorCode.CART_REGISTER_FAIL_ITEM_EXIST);
        }
    }

}
