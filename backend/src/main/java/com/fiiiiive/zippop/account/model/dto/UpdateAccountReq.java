package com.fiiiiive.zippop.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 정보 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountReq {
    private String name;
    private String crn;
    private String phoneNumber;
    private String address;
    private String profileImageUrl;
}


