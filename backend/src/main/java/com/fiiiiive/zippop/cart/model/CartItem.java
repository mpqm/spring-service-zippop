package com.fiiiiive.zippop.cart.model;

import com.fiiiiive.zippop.goods.model.Goods;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem {

    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 상품 수량 (필수, 최소 1)
    @Column(nullable = false)
    @Min(value = 1, message = "상품 수량은 최소 1개 이상이어야 합니다.")
    private Integer quantity;

    // 총 가격 (필수, 0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "총 가격은 0 이상이어야 합니다.")
    private Integer price;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx")
    private Goods goods;

    // toDto
    public CartDto.GetCartItemRes toDto() {
        return CartDto.GetCartItemRes.builder()
                .cartItemIdx(this.getIdx())
                .count(this.getQuantity())
                .price(this.getPrice())
                .searchGoodsRes(this.getGoods().toDto())
                .build();
    }

    public static List<CartDto.GetCartItemRes> toDtoList(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(CartItem::toDto)
                .collect(Collectors.toList());
    }

}
