package com.fiiiiive.zippop.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {
    private String userId;
    private String password;
}