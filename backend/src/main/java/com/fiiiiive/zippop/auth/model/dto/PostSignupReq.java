package com.fiiiiive.zippop.auth.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSignupReq {
    private String role;
    private String userId;
    private String email;
    private String password;
    private String name;
    private String crn;
    private String phoneNumber;
    private String address;
}
