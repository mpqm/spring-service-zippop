package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_intent")
public class PaymentIntent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true, length = 100)
    private String merchantUid;

    @Column(nullable = false)
    private Long accountIdx;

    @Column(nullable = false)
    private Long popupIdx;

    private Long reserveIdx;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String goodsJson;

    @Column(nullable = false)
    private Integer usedPoint;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(unique = true, length = 100)
    private String impUid;

    private Long orderIdx;

    @Column(nullable = false)
    private java.time.LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentIntentType type;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentIntentStatus status;

    public static PaymentIntent ready(String merchantUid, Long accountIdx, Long popupIdx, Long reserveIdx,
                                      String goodsJson, Integer usedPoint, Integer totalPrice) {
        return PaymentIntent.builder()
                .merchantUid(merchantUid)
                .accountIdx(accountIdx)
                .popupIdx(popupIdx)
                .reserveIdx(reserveIdx)
                .goodsJson(goodsJson)
                .usedPoint(usedPoint)
                .totalPrice(totalPrice)
                .expiresAt(java.time.LocalDateTime.now().plusMinutes(15))
                .type(reserveIdx == null ? PaymentIntentType.STOCK : PaymentIntentType.RESERVE)
                .status(PaymentIntentStatus.READY)
                .build();
    }

    public void complete(String impUid, Long orderIdx) {
        this.impUid = impUid;
        this.orderIdx = orderIdx;
        this.status = PaymentIntentStatus.COMPLETED;
    }
}
