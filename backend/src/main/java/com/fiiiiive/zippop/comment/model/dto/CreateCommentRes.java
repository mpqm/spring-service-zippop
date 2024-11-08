package com.fiiiiive.zippop.comment.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRes {
    private Long commentIdx;
}
