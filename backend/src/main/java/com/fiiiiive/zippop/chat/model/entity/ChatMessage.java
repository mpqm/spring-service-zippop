package com.fiiiiive.zippop.chat.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatMessage extends BaseEntity {
    // Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String sender;
    private String content;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_idx")
    private ChatRoom chatRoom;
}