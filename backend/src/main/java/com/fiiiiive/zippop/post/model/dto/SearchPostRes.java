package com.fiiiiive.zippop.post.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchPostRes {
    private Long postIdx;
    private String customerEmail;
    private String postTitle;
    private String postContent;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SearchPostImageRes> searchPostImageResList;
}
