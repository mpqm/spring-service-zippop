package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrders extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerOrdersIdx;
    @Column(nullable = false, length = 100, unique = true)
    private String impUid;
    private Integer usedPoint;
    private Integer totalPrice;
    private String orderState;
    private Integer deliveryCost;

    // OneToMany
    @OneToMany(mappedBy = "customerOrders")
    private List<CustomerOrdersDetail> customerOrdersDetailList;

    // ManyToOne
    @ManyToOne
    @JoinColumn(name = "customer_idx")
    private Customer customer;


}