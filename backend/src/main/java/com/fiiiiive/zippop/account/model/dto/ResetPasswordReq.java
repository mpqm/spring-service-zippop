package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// PW 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReq {

    @NotNull(message = "이전 패스워드는 필수 입력 항목입니다.")
    private String originPassword;

    @NotNull(message = "새로운 패스워드는 필수 입력 항목입니다.")
    private String newPassword;

}