package com.fiiiiive.zippop.post.model.entity;

import com.fiiiiive.zippop.common.base.BaseEntity;
import com.fiiiiive.zippop.member.model.entity.Customer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PostLike extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_idx")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_idx")
    private Customer customer;
}
