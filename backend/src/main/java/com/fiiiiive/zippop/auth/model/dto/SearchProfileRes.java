package com.fiiiiive.zippop.auth.model.dto;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class SearchProfileRes {
    private String name;
    private String email;
    private Integer point;
    private String phoneNumber;
    private String address;
    private String crn;
}
