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
public class Orders extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false, length = 100, unique = true)
    private String impUid;
    private Integer totalPrice;
    private Integer usedPoint;
    private Integer deliveryCost;
    private String orderState;

    // OneToMany
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersDetail> ordersDetailList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;
}