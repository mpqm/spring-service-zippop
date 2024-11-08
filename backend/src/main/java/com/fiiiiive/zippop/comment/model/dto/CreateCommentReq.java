package com.fiiiiive.zippop.comment.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentReq {
    private String content;
}