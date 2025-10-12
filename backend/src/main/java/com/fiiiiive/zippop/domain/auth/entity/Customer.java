package com.fiiiiive.zippop.domain.auth.entity;

import com.fiiiiive.zippop.domain.auth.dto.AuthDto;
import com.fiiiiive.zippop.domain.cart.entity.Cart;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.domain.orders.entity.Orders;
import com.fiiiiive.zippop.domain.store.entity.StoreLike;
import com.fiiiiive.zippop.domain.store.entity.StoreReview;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer extends BaseEntity {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 사용자 ID (필수, 유니크, 최소 5자, 최대 20자)
    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    // 이메일 (필수, 유니크, 최대 100자)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 이름 (필수, 최대 50자)
    @Column(nullable = false, length = 50)
    private String name;

    // 전화번호 (필수, 최대 15자)
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    // 주소 (필수, 최대 200자)
    @Column(nullable = false, length = 200)
    private String address;

    // 역할 (필수, 최대 20자, ROLE_CUSTOMER로 고정)
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
    private Integer point; // 포인트

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
    @OneToMany(mappedBy = "customer")
    private List<Cart> cartList;

    @OneToMany(mappedBy = "customer")
    private List<StoreReview> storeReviewList;

    @OneToMany(mappedBy = "customer")
    private List<Orders> ordersList;

    @OneToMany(mappedBy = "customer")
    private List<StoreLike> storeLikeList;

    // Update
    public Customer update(AuthDto.EditInfoReq dto, String profileImageUrl) {
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.phoneNumber = dto.getPhoneNumber();
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    // toDto
    public AuthDto.GetInfoRes toGetInfoRes(){
        return AuthDto.GetInfoRes.builder()
                .name(this.getName())
                .point(this.getPoint())
                .role(this.getRole().name())
                .profileImageUrl(this.getProfileImageUrl())
                .email(this.getEmail())
                .phoneNumber(this.getPhoneNumber())
                .address(this.getAddress())
                .build();
    }

}
