package com.fiiiiive.zippop.auth.model.entity;

import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
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
    private String userId;
    private String name;
    private String crn; // 사업자 등록 번호
    private String phoneNumber;
    private String address;
    private String role;
    private String profileImageUrl;

    // Setter
    @Setter
    private Boolean isEmailAuth;
    @Setter
    private Boolean isInActive;
    @Setter
    private String password;

    // Update
    public Company update(String name, String address, String crn, String phoneNumber, String profileImageUrl) {
        this.name = name;
        this.address = address;
        this.crn = crn;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    // OneToMany
    @OneToMany(mappedBy = "company")
    private List<Store> storeList;
}

