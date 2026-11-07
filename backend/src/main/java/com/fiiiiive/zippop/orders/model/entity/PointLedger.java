package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "point_ledger")
public class PointLedger extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_idx", nullable = false)
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orders_idx", nullable = false)
    private Orders orders;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PointLedgerType type;
    @Column(nullable = false)
    private Integer amount;
    @Column(nullable = false)
    private Integer balanceAfter;

    public static PointLedger create(Customer customer, Orders orders, PointLedgerType type,
                                     int amount, int balanceAfter) {
        return PointLedger.builder().customer(customer).orders(orders).type(type)
                .amount(amount).balanceAfter(balanceAfter).build();
    }
}
