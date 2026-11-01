package com.fiiiiive.zippop.global.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestUrlService {

    private final HttpServletRequest request;

    public String emailVerificationUrl(String email, String role, String uuid) {
        return ServletUriComponentsBuilder.fromContextPath(request)
                .path("/api/v1/accounts/verification")
                .queryParam("email", "{email}")
                .queryParam("role", "{role}")
                .queryParam("uuid", "{uuid}")
                .encode()
                .buildAndExpand(Map.of("email", email, "role", role, "uuid", uuid))
                .toUriString();
    }

    public String loginResultUrl(boolean verified) {
        return ServletUriComponentsBuilder.fromContextPath(request)
                .path("/login")
                .queryParam(verified ? "success" : "error", true)
                .build()
                .toUriString();
    }
}
