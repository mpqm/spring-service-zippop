package com.fiiiiive.zippop.member.model.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditPasswordReq {
    private String originPassword;
    private String newPassword;
}
