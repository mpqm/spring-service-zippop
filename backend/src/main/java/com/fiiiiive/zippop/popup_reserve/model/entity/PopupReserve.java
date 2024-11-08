package com.fiiiiive.zippop.popup_reserve.model.entity;


import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.popup_store.model.entity.PopupStore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupReserve extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reserveIdx;
    private String reserveUUID;
    private String reserveWaitingUUID;
    private Long maxCount;
    private Long expiredTime; // 날짜 형식으로 변환

    @ManyToOne
    @JoinColumn(name ="store_idx")
    private PopupStore popupStore;
}
