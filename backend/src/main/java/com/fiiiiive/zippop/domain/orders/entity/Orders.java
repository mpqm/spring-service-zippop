package com.fiiiiive.zippop.domain.orders.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.domain.auth.entity.Customer;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.domain.orders.dto.OrdersDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orders extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 100, unique = true)
    private String impUid;

    @Column(nullable = false)
    @PositiveOrZero(message = "총 가격은 0 이상이어야 합니다.")
    private Integer totalPrice;

    @Column(nullable = false)
    @PositiveOrZero(message = "사용 포인트는 0 이상이어야 합니다.")
    private Integer usedPoint;

    // 배송비 (0 이상)
    @Column(nullable = false)
    @PositiveOrZero(message = "배송비는 0 이상이어야 합니다.")
    private Integer deliveryCost;

    // 주문 상태 (필수, 최대 50자)
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @Column(nullable = false)
    private Long storeIdx;

    // OneToMany
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersDetail> ordersDetailList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    // ToDto
    public OrdersDto.SearchOrdersRes toDto() {
        return OrdersDto.SearchOrdersRes.builder()
                .ordersIdx(this.getIdx())
                .impUid(this.getImpUid())
                .name(this.getCustomer().getName())
                .email(this.getCustomer().getEmail())
                .address(this.getCustomer().getAddress())
                .phoneNumber(this.getCustomer().getPhoneNumber())
                .usedPoint(this.getUsedPoint())
                .totalPrice(this.getTotalPrice())
                .orderStatus(this.getStatus().name())
                .deliveryCost(this.getDeliveryCost())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .searchOrdersDetailResList(OrdersDetail.toDtoList(this.getOrdersDetailList()))
                .build();
    }

    public static Page<OrdersDto.SearchOrdersRes> toDtoPage(Page<Orders> ordersPage) {
        return ordersPage.map(Orders::toDto);
    }
}