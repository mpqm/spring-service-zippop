package com.fiiiiive.zippop.auth.model.entity;

import com.fiiiiive.zippop.auth.model.dto.AuthDto;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseStatus;
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
    // 이메일 (필수, 유니크, 최대 100자)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 사용자 ID (필수, 유니크, 최소 5자, 최대 20자)
    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    // 이름 (필수, 최대 50자)
    @Column(nullable = false, length = 50)
    private String name;

    // 사업자 등록 번호 (선택, 최대 15자)
    @Column(length = 15)
    private String crn;

    // 전화번호 (필수, 최대 15자)
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    // 주소 (필수, 최대 200자)
    @Column(nullable = false, length = 200)
    private String address;

    // 역할 (필수, 최대 20자, ROLE_CUSTOMER 또는 ROLE_COMPANY)
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus role;

    // 프로필 이미지 URL (선택, 최대 255자)
    @Column(length = 255)
    private String profileImageUrl;

    // Setter
    @Setter
    @Column(nullable = false)
    private Boolean isEmailAuth;

    @Setter
    @Column(nullable = false)
    private Boolean isInActive;

    @Setter
    @Column(nullable = false)
    private String password;

    // OneToMany
    @OneToMany(mappedBy = "company")
    private List<Store> storeList;

    // Update
    public Company update(AuthDto.EditInfoReq dto, String profileImageUrl) {
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.crn = dto.getCrn();
        this.phoneNumber = dto.getPhoneNumber();
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    // ToDto
    public AuthDto.GetInfoRes toGetInfoRes(){
        return AuthDto.GetInfoRes.builder()
                .name(this.getName())
                .crn(this.getCrn())
                .role(this.getRole().name())
                .profileImageUrl(this.getProfileImageUrl())
                .email(this.getEmail())
                .phoneNumber(this.getPhoneNumber())
                .address(this.getAddress())
                .build();
    }

}

