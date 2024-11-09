package com.fiiiiive.zippop.auth.model.entity;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.comment.model.entity.Comment;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.favorite.model.entity.Favorite;
import com.fiiiiive.zippop.orders.model.entity.CustomerOrders;
import com.fiiiiive.zippop.review.model.entity.Review;
import com.fiiiiive.zippop.post.model.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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

    // OneToMany
    @BatchSize(size=10)
    @OneToMany(mappedBy = "customer")
    private List<Post> postList;

    @OneToMany(mappedBy = "customer")
    private List<Cart> cartList;

    @OneToMany(mappedBy = "customer")
    private List<Favorite> favoriteList;

    @BatchSize(size=10)
    @OneToMany(mappedBy = "customer")
    private List<Comment> commentList;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviewList;

    @OneToMany(mappedBy = "customer")
    private List<CustomerOrders> customerOrdersList;
}
