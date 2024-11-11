package com.fiiiiive.zippop.auth.model.dto;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class GetInfoRes {
    private String name;
    private String email;
    private String role;
    private Integer point;
    private String phoneNumber;
    private String address;
    private String crn;
    private String profileImageUrl;
}
