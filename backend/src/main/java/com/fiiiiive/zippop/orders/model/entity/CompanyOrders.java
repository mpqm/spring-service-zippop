package com.fiiiiive.zippop.orders.model.entity;


import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.auth.model.entity.Company;
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
public class CompanyOrders extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false, length = 100, unique = true)
    private String impUid;

    // OneToMany
    @OneToMany(mappedBy = "companyOrders")
    private List<CompanyOrdersDetail> companyOrdersDetailList = new ArrayList<>();

    // ManyToOne
    @ManyToOne
    @JoinColumn(name = "companyIdx")
    private Company company;

}
