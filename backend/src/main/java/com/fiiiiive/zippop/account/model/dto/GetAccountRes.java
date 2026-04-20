package com.fiiiiive.zippop.account.model.dto;

import lombok.Builder;
import lombok.Getter;

// 유저 정보 응답 DTO
@Getter
@Builder
public class GetAccountRes {
    private String name;
    private String email;
    private String role;
    private Integer point;
    private String phoneNumber;
    private String address;
    private String crn;
    private String profileImageUrl;
}
