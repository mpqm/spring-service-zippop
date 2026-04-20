package com.fiiiiive.zippop.popup.model.entity;

import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.popup.model.dto.GetPopupReviewRes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupReview extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    // 고객 이메일 (필수, 최대 100자)
    @Column(nullable = false, length = 100)
    private String customerEmail;

    // 고객 이름 (필수, 최대 50자)
    @Column(nullable = false, length = 50)
    private String customerName;

    // 리뷰 제목 (필수, 최대 100자)
    @Column(nullable = false, length = 100)
    private String title;

    // 리뷰 내용 (필수, 최대 1000자)
    @Column(nullable = false, length = 1000)
    private String content;

    // 평점 (필수, 1 ~ 5 범위)
    @Column(nullable = false)
    @Min(value = 1, message = "평점은 최소 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5 이하여야 합니다.")
    private Integer rating;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx")
    private Popup popup;

    // create
    public static PopupReview create(
            Long customerIdx,
            String customerEmail,
            String customerName,
            Popup popup,
            String title,
            String content,
            Integer rating
    ) {
        Customer customerRef = Customer.builder()
                .idx(customerIdx)
                .build();
        return PopupReview.builder()
                .customer(customerRef)
                .popup(popup)
                .customerEmail(customerEmail)
                .customerName(customerName)
                .title(title)
                .content(content)
                .rating(rating)
                .build();
    }

    // ToDto
    public GetPopupReviewRes toDto() {
        return GetPopupReviewRes.builder()
                .reviewIdx(this.getIdx())
                .popupName(this.getPopup().getName())
                .customerName(this.getCustomerName())
                .customerEmail(this.getCustomerEmail())
                .reviewTitle(this.getTitle())
                .reviewContent(this.getContent())
                .reviewRating(this.getRating())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static Page<GetPopupReviewRes> toDtoPage(Page<PopupReview> popupReviewPage) {
        return popupReviewPage.map(PopupReview::toDto);
    }
}
