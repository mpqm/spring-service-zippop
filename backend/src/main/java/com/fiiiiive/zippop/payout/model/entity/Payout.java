package com.fiiiiive.zippop.payout.model.entity;

import com.fiiiiive.zippop.payout.model.dto.GetPayoutsRes;
import com.fiiiiive.zippop.payout.model.enums.PayoutStatus;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 총 매출 (필수, 0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "총 매출은 0 이상이어야 합니다.")
    private Integer revenue;

    // 정산일 (필수)
    @Column(nullable = false)
    private LocalDate payoutDate;

    // 정산 상태 (필수, 최대 50자)
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutStatus status;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx", nullable = false)
    private Popup popup;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // create
    public static Payout create(Popup popup, Integer revenue, LocalDate date) {
        return Payout.builder()
                .popup(popup)
                .revenue(revenue)
                .payoutDate(date)
                .status(PayoutStatus.PAYOUT_COMPLETE)
                .build();
    }

    public GetPayoutsRes toDto() {
        return GetPayoutsRes.builder()
                .payoutDate(this.getPayoutDate())
                .revenue(this.getRevenue())
                .build();
    }

    public static Page<GetPayoutsRes> toDtoPage(Page<Payout> payoutPage) {
        return payoutPage.map(Payout::toDto);
    }
}

