package com.fiiiiive.zippop.settlement.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.settlement.dto.SettlementDto;
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
public class Settlement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 정산 대상 스토어 ID (필수)
    @Column(nullable = false)
    private Long storeIdx;

    // 총 매출 (필수, 0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "총 매출은 0 이상이어야 합니다.")
    private Integer totalRevenue;

    // 정산일 (필수)
    @Column(nullable = false)
    private LocalDate settlementDate;

    // 정산 상태 (필수, 최대 50자)
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    public SettlementDto.SearchSettlementRes toDto() {
        return SettlementDto.SearchSettlementRes.builder()
                .settlementDate(this.getSettlementDate())
                .totalRevenue(this.getTotalRevenue())
                .build();
    }

    public static Page<SettlementDto.SearchSettlementRes> toDtoPage(Page<Settlement> settlementPage) {
        return settlementPage.map(Settlement::toDto);
    }
}

