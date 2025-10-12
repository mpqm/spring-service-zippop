package com.fiiiiive.zippop.domain.payout.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.domain.payout.dto.PayoutDto;
import com.fiiiiive.zippop.domain.store.entity.Store;
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
    private Integer totalRevenue;

    // 정산일 (필수)
    @Column(nullable = false)
    private LocalDate payoutDate;

    // 정산 상태 (필수, 최대 50자)
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx", nullable = false)
    private Store store;

    public PayoutDto.SearchPayoutRes toDto() {
        return PayoutDto.SearchPayoutRes.builder()
                .payoutDate(this.getPayoutDate())
                .totalRevenue(this.getTotalRevenue())
                .build();
    }

    public static Page<PayoutDto.SearchPayoutRes> toDtoPage(Page<Payout> payoutPage) {
        return payoutPage.map(Payout::toDto);
    }
}

