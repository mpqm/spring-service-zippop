package com.fiiiiive.zippop.popup_review.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.popup_store.model.entity.PopupStore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PopupReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewIdx;
    private String customerEmail;
    private String reviewTitle;
    private String reviewContent;
    private Integer rating;


    @OneToMany(mappedBy = "popupReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupReviewImage> popupReviewImageList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerIdx")
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeIdx")
    @JsonBackReference
    private PopupStore popupStore;
}

