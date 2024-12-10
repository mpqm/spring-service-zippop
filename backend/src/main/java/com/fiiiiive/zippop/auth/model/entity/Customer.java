package com.fiiiiive.zippop.auth.model.entity;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.commerce.model.entity.Orders;
import com.fiiiiive.zippop.store.model.entity.StoreReview;
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
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private String role;
    private String profileImageUrl;

    // Setter
    @Setter
    private Integer point; // 포인트
    @Setter
    private Boolean isEmailAuth;
    @Setter
    private Boolean isInActive;
    @Setter
    private String password;

    // Update
    public Customer update(String name, String address, String phoneNumber, String profileImageUrl) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    // OneToMany
    @OneToMany(mappedBy = "customer")
    private List<Cart> cartList;

    @OneToMany(mappedBy = "customer")
    private List<StoreReview> storeReviewList;

    @OneToMany(mappedBy = "customer")
    private List<Orders> ordersList;
}
