package com.fiiiiive.zippop.comment.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentRes {
    private Long commentIdx;
    private String customerEmail;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}