package com.fiiiiive.zippop.store.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.member.model.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreLike extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_idx")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_idx")
    private Customer customer;
}

