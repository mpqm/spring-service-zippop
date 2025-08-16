package com.fiiiiive.zippop.reserve.model.entity;


import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.reserve.model.dto.ReserveDto;
import com.fiiiiive.zippop.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Page;

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

    // Working UUID (필수, 최대 길이 36)
    @Column(nullable = false, length = 36)
    private String workingUUID;

    // Waiting UUID (필수, 최대 길이 36)
    @Column(nullable = false, length = 36)
    private String waitingUUID;

    // 총 인원수 (필수, 최소값 1)
    @Column(nullable = false)
    private Integer totalPeople;

    // 예약 시작 날짜 (필수)
    @Column(nullable = false)
    private LocalDate startDate;

    // 예약 시작 시간 (필수)
    @Column(nullable = false)
    private LocalDateTime startTime;

    // 예약 종료 시간 (필수, 시작 시간보다 나중이어야 함)
    @Column(nullable = false)
    private LocalDateTime endTime;

    // ManyToOne
    @ManyToOne
    @JoinColumn(name ="store_idx")
    private Store store;

    public ReserveDto.SearchReserveRes toDto(){
        return ReserveDto.SearchReserveRes.builder()
                .storeIdx(this.getStore().getIdx())
                .reserveIdx(this.getIdx())
                .reservePeople(this.getTotalPeople())
                .reserveStartDate(this.getStartDate())
                .reserveStartTime(this.getStartTime())
                .reserveEndTime(this.getEndTime())
                .searchStoreRes(store.toDto())
                .build();
    }


    public static Page<ReserveDto.SearchReserveRes> toDtoPage(Page<Reserve> reservePage) {
        return reservePage.map(Reserve::toDto);
    }

}
