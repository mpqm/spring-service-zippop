package com.fiiiiive.zippop.popup.model;

import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupLike extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="popup_idx")
    private Popup popup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_idx")
    private Customer customer;

    // ToDTO
    public PopupDto.GetPopupRes toDto() {
        return PopupDto.GetPopupRes.builder()
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
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .getPopupImageResList(PopupImage.toDtoList(this.getPopup().getPopupImageList()))
                .build();
    }

    public static Page<PopupDto.GetPopupRes> toDtoPage(Page<PopupLike> popupLikePage) {
        return popupLikePage.map(PopupLike::toDto);
    }

    public static PopupLike create(Popup popup, Long userIdx) {
        Customer customerRef = Customer.builder()
                .idx(userIdx)
                .build();
        return PopupLike.builder()
                .popup(popup)
                .customer(customerRef)
                .build();
    }

}
