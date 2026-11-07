package com.fiiiiive.zippop.account.model.entity;

import com.fiiiiive.zippop.account.model.dto.GetAccountRes;
import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_idx", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false, length = 15)
    private String crn;

    public void updateCrn(String crn) { this.crn = crn; }

    public GetAccountRes toDto() {
        return GetAccountRes.builder()
                .name(account.getName())
                .email(account.getEmail())
                .role(account.getRole().name())
                .crn(crn)
                .phoneNumber(account.getPhoneNumber())
                .address(account.getAddress())
                .profileImageUrl(account.getProfileImageUrl())
                .build();
    }
}
