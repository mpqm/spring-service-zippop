package com.fiiiiive.zippop.auth.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostLoginRes {
    private String accessToken;
}