package com.fiiiiive.zippop.payout.model;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.enums.PayoutStatus;
import com.fiiiiive.zippop.popup.model.Popup;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payout extends BaseEntity {
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
    private String status;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx", nullable = false)
    private Popup popup;

    // create
    public static Payout create(Popup popup, Integer revenue, LocalDate date) {
        return Payout.builder()
                .popup(popup)
                .revenue(revenue)
                .payoutDate(date)
                .status(PayoutStatus.PAYOUT_COMPLETE.name())
                .build();
    }

    public PayoutDto.SearchPayoutRes toDto() {
        return PayoutDto.SearchPayoutRes.builder()
                .payoutDate(this.getPayoutDate())
                .revenue(this.getRevenue())
                .build();
    }

    public static Page<PayoutDto.SearchPayoutRes> toDtoPage(Page<Payout> payoutPage) {
        return payoutPage.map(Payout::toDto);
    }
}

