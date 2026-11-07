package com.fiiiiive.zippop.account.model.dto;

import lombok.Builder;
import lombok.Getter;

// 회원 가입 응답 DTO
@Getter
@Builder
public class CreateAccountRes {

    private Boolean isExistUser;

}
