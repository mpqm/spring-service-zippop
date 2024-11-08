package com.fiiiiive.zippop.post.model.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostReq {
    private String postTitle;
    private String postContent;
}
