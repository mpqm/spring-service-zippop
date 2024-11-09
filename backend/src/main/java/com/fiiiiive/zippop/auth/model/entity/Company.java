package com.fiiiiive.zippop.auth.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
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
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String email;
    private String password;
    private String name;
    private String crn; // 사업자 등록 번호
    private String phoneNumber;
    private String address;
    private Boolean isEmailAuth;
    private Boolean isInActive;
    private String role;
    private String profileImageUrl;

    // OneToMany
    @OneToMany(mappedBy = "company")
    private List<Store> storeList;

    @OneToMany(mappedBy = "company")
    private List<CompanyOrders> companyOrdersList;
}

