package com.fiiiiive.zippop.orders.model.entity;


import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.member.model.entity.Company;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanyOrders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false, length = 100, unique = true)
    private String impUid;
    //
    @ManyToOne
    @JoinColumn(name = "companyIdx")
    private Company company;
    @OneToMany(mappedBy = "companyOrders")
    private List<CompanyOrdersDetail> companyOrdersDetailList = new ArrayList<>();
}
