package com.fiiiiive.zippop.orders.model.entity;

import com.fiiiiive.zippop.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refund_outbox", uniqueConstraints = @UniqueConstraint(name = "uk_refund_outbox_imp_uid", columnNames = "imp_uid"))
public class RefundOutbox extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(name = "imp_uid", nullable = false, length = 100)
    private String impUid;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_idx")
    private Orders orders;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundOutboxStatus status;
    @Column(nullable = false)
    private Integer attempts;
    @Column(nullable = false)
    private LocalDateTime nextAttemptAt;
    @Column(length = 1000)
    private String lastError;
    @Version
    private Long version;

    public static RefundOutbox pending(String impUid, BigDecimal amount, Orders orders) {
        return RefundOutbox.builder().impUid(impUid).amount(amount).orders(orders)
                .status(RefundOutboxStatus.PENDING).attempts(0).nextAttemptAt(LocalDateTime.now()).build();
    }
    public void completed() {
        status = RefundOutboxStatus.COMPLETED;
        lastError = null;
        if (orders != null) orders.markRefundCompleted();
    }
    public void retry(String error) {
        attempts++;
        status = RefundOutboxStatus.RETRY;
        lastError = error == null ? "PG refund failed" : error.substring(0, Math.min(error.length(), 1000));
        long delayMinutes = Math.min(60, 1L << Math.min(attempts, 6));
        nextAttemptAt = LocalDateTime.now().plusMinutes(delayMinutes);
        if (orders != null) orders.markRefundFailed();
    }
}
