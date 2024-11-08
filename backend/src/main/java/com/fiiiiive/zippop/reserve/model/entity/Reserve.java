package com.fiiiiive.zippop.reserve.model.entity;


import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

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
    private String OnDoingUUID;
    private String WaitingUUID;
    private Long maxCount;
    private Long expiredTime;

    // ManyToOne
    @ManyToOne
    @JoinColumn(name ="store_idx")
    private Store store;
}
