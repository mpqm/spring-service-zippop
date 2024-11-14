package com.fiiiiive.zippop.auth.model.entity;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.orders.model.entity.CustomerOrders;
import com.fiiiiive.zippop.store.model.entity.StoreReview;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
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
    private String password;
    private String name;
    private String phoneNumber;
    private String address;
    private Integer point;
    private String role;
    private Boolean isEmailAuth;
    private Boolean isInActive;
    private String profileImageUrl;


    @OneToMany(mappedBy = "customer")
    private List<Cart> cartList;

    @OneToMany(mappedBy = "customer")
    private List<StoreReview> storeReviewList;

    @OneToMany(mappedBy = "customer")
    private List<CustomerOrders> customerOrdersList;
}
