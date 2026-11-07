package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 계정 활성화 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountReq {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

}
