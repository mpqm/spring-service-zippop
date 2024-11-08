package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.member.model.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerOrdersIdx;
    @Column(nullable = false, length = 100, unique = true)
    private String impUid;
    private Integer usedPoint;
    private Integer totalPrice;
    private String orderState;
    private Integer deliveryCost;

    @ManyToOne
    @JoinColumn(name = "customerIdx")
    private Customer customer;

    @OneToMany(mappedBy = "customerOrders")
    private List<CustomerOrdersDetail> customerOrdersDetailList = new ArrayList<>();
}