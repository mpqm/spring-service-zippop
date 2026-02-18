package com.fiiiiive.zippop.orders.model;

import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.account.model.Customer;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.popup.model.Popup;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orders extends BaseEntity {

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
    private OrdersStatus status;

    // OneToMany
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersDetail> ordersDetailList;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_idx", nullable = false)
    private Popup popup;


    // 예약 굿즈 주문 저장
    public static Orders createReserveOrders(String impUid, Integer totalPurchasePrice, Customer customer, Popup popup) {
        return Orders.builder()
                .impUid(impUid)
                .totalPrice(totalPurchasePrice)
                .status(OrdersStatus.RESERVE_READY)
                .deliveryCost(0)
                .usedPoint(0)
                .customer(customer)
                .popup(popup)
                .build();
    }
    // 재고 굿즈 주문 저장
    public static Orders createStockOrders(String impUid, Integer totalPurchasePrice, Integer usedPoint, Customer customer, Popup popup) {
        return Orders.builder()
                .impUid(impUid)
                .totalPrice(totalPurchasePrice)
                .status(OrdersStatus.STOCK_READY)
                .deliveryCost(2500)
                .usedPoint(usedPoint)
                .customer(customer)
                .popup(popup)
                .build();
    }


    // ToDto
    public OrdersDto.GetOrdersRes toDto() {
        return OrdersDto.GetOrdersRes.builder()
                .ordersIdx(this.getIdx())
                .impUid(this.getImpUid())
                .name(this.getCustomer().getName())
                .email(this.getCustomer().getEmail())
                .address(this.getCustomer().getAddress())
                .phoneNumber(this.getCustomer().getPhoneNumber())
                .usedPoint(this.getUsedPoint())
                .totalPrice(this.getTotalPrice())
                .orderStatus(this.getStatus().getName())
                .deliveryCost(this.getDeliveryCost())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .getOrdersDetailResList(OrdersDetail.toDtoList(this.getOrdersDetailList()))
                .build();
    }

    public static Page<OrdersDto.GetOrdersRes> toDtoPage(Page<Orders> ordersPage) {
        return ordersPage.map(Orders::toDto);
    }

    public void updateOrderStatus(){
        if(Objects.equals(this.status, OrdersStatus.STOCK_READY) || Objects.equals(this.status, OrdersStatus.STOCK_COMPLETE)){
            this.status = OrdersStatus.STOCK_CANCEL;
        } else if (Objects.equals(this.status, OrdersStatus.RESERVE_READY) || Objects.equals(this.status, OrdersStatus.RESERVE_COMPLETE)){
            this.status = OrdersStatus.RESERVE_CANCEL;
        } else {
            throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        }
    }

    public void validateOrders(){
        if(Objects.equals(this.status, OrdersStatus.STOCK_DELIVERY) || Objects.equals(this.status, OrdersStatus.RESERVE_DELIVERY)) {
            throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        }
        if(Objects.equals(this.getStatus(), OrdersStatus.STOCK_CANCEL) || Objects.equals(this.getStatus(), OrdersStatus.RESERVE_CANCEL)) {
            throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_ALREADY_CANCEL);
        }
    }

    public void changeToDelivery() {
        switch (this.status) {
            case STOCK_READY, STOCK_COMPLETE -> this.status = OrdersStatus.STOCK_DELIVERY;
            case RESERVE_READY, RESERVE_COMPLETE -> this.status = OrdersStatus.RESERVE_DELIVERY;
            case STOCK_DELIVERY, RESERVE_DELIVERY -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_DELIVERY);
            case STOCK_CANCEL, RESERVE_CANCEL -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_CANCEL);
        }
    }

    public void changeToComplete() {
        switch (this.status) {
            case STOCK_READY -> this.status = OrdersStatus.STOCK_COMPLETE;
            case RESERVE_READY -> this.status = OrdersStatus.RESERVE_COMPLETE;
            case STOCK_DELIVERY, RESERVE_DELIVERY -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_DELIVERY);
            case STOCK_CANCEL, RESERVE_CANCEL -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_CANCEL);
        }
    }

}