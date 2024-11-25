package com.fiiiiive.zippop.auth.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditInfoReq {
    private String name;
    private String crn;
    private String phoneNumber;
    private String address;
    private String profileImageUrl;
}
