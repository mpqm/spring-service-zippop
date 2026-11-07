package com.fiiiiive.zippop.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPwReq {

    @NotBlank(message = "비밀번호 재설정 토큰은 필수 입력 항목입니다.")
    private String token;

    @NotBlank(message = "새로운 패스워드는 필수 입력 항목입니다.")
    @Size(min = 8, max = 72, message = "새로운 패스워드는 8자 이상, 72자 이하여야 합니다.")
    private String newPassword;

}
