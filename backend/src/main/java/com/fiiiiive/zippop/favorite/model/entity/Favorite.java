package com.fiiiiive.zippop.favorite.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.popup_store.model.entity.PopupStore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerIdx")
    @JsonIgnore
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeIdx")
    @JsonIgnore
    private PopupStore popupStore;
}