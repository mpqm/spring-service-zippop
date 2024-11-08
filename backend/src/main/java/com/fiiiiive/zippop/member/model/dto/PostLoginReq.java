package com.fiiiiive.zippop.member.model.dto;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLoginReq {
    private String email;
    private String password;
}
