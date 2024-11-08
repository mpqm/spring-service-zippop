package com.fiiiiive.zippop.orders.model.entity;

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
    private Store store;
}
