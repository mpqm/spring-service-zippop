package com.fiiiiive.zippop.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// AccessToken을 Authorization Header에 담았을때 사용했음
@Getter
@Builder
@AllArgsConstructor
public class PostSignupRes {
    private Long idx;
    private String userId;
    private Boolean isEmailAuth;
    private Boolean isInactive;
    private String role;
    private String email;
}

