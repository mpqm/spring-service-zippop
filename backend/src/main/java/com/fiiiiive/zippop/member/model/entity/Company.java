package com.fiiiiive.zippop.member.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.orders.model.entity.CompanyOrders;
import com.fiiiiive.zippop.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyIdx;
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    @Column(nullable = false, length = 100, unique = true)
    private String password;
    private String name;
    private String crn; // 사업자 등록 번호
    private String phoneNumber;
    private String address;
    private Boolean enabled;
    private Boolean inactive;
    private String role;

    @OneToMany(mappedBy = "company")
    @JsonManagedReference
    private List<Store> storeList = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<CompanyOrders> companyOrdersList = new ArrayList<>();
}

