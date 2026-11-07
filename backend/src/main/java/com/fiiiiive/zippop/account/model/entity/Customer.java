package com.fiiiiive.zippop.account.model.entity;

import com.fiiiiive.zippop.account.model.dto.GetAccountRes;
import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor @Entity
public class Customer extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_idx", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false)
    private Integer point;

    public String getName() { return account.getName(); }

    public String getEmail() { return account.getEmail(); }

    public String getPhoneNumber() { return account.getPhoneNumber(); }

    public String getAddress() { return account.getAddress(); }

    public void updatePoint(Integer point) { this.point = point; }

    public GetAccountRes toDto() {
        return GetAccountRes.builder()
                .name(account.getName())
                .email(account.getEmail())
                .role(account.getRole().name())
                .point(point)
                .phoneNumber(account.getPhoneNumber())
                .address(account.getAddress())
                .profileImageUrl(account.getProfileImageUrl()).build();
    }
}
