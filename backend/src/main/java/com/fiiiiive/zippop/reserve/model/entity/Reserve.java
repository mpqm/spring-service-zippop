package com.fiiiiive.zippop.reserve.model.entity;


import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserve extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String workingUUID;
    private String waitingUUID;
    private Integer totalPeople;
    private LocalDate startDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // ManyToOne
    @ManyToOne
    @JoinColumn(name ="store_idx")
    private Store store;
}
