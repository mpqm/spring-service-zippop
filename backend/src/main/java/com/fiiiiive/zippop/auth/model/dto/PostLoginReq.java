package com.fiiiiive.zippop.auth.model.dto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {
    private String userId;
    private String password;
}
