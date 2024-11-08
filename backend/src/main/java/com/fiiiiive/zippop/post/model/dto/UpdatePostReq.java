package com.fiiiiive.zippop.post.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostReq {
    private String title;
    private String content;
}
