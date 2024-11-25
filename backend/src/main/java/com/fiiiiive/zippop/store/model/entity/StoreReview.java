package com.fiiiiive.zippop.store.model.entity;

import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreReview extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String customerEmail;
    private String customerName;
    private String title;
    private String content;
    private Integer rating; // 평점

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_idx")
    private Store store;
}

