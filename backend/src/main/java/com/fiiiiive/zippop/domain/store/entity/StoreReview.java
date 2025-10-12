package com.fiiiiive.zippop.domain.store.entity;

import com.fiiiiive.zippop.domain.auth.entity.Customer;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.domain.store.dto.StoreDto;
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
public class StoreReview extends BaseEntity {
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
    @JoinColumn(name = "store_idx")
    private Store store;

    // ToDto
    public StoreDto.SearchStoreReviewRes toDto() {
        return StoreDto.SearchStoreReviewRes.builder()
                .reviewIdx(this.getIdx())
                .storeName(this.getStore().getName())
                .customerName(this.getCustomerName())
                .customerEmail(this.getCustomerEmail())
                .reviewTitle(this.getTitle())
                .reviewContent(this.getContent())
                .reviewRating(this.getRating())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static Page<StoreDto.SearchStoreReviewRes> toDtoPage(Page<StoreReview> storeReviewPage) {
        return storeReviewPage.map(StoreReview::toDto);
    }
}

