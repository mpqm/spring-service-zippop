package com.fiiiiive.zippop.post.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fiiiiive.zippop.comment.model.entity.Comment;
import com.fiiiiive.zippop.global.common.base.BaseEntity;
import com.fiiiiive.zippop.member.model.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postIdx;
    private String email;
    private String title;
    private String content;
    private Integer likeCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikeList;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImageList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerIdx")
    @JsonBackReference
    private Customer customer;
}
