package com.fiiiiive.zippop.commerce.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column
    private Long storeIdx; // 정산 대상 스토어 ID

    @Column
    private Integer totalRevenue; // 총 매출

    @Column
    private LocalDate settlementDate; // 정산일

    @Column
    private String status; // 정산 상태
}

