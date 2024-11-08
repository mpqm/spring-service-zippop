package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.popup_store.model.entity.PopupStore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanyOrdersDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyOrderDetailIdx;
    private Integer totalPrice;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyOrdersIdx")
    private CompanyOrders companyOrders;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popupStoreIdx")
    private PopupStore popupStore;
}
