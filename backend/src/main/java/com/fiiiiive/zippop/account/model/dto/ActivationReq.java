package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 계정 활성화 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationReq {

    @NotNull(message = "역할은 필수 입력 항목입니다.")
    private String role;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

}
