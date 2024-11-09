package com.fiiiiive.zippop.test;

import com.fiiiiive.zippop.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    // 접근 권한 테스트
    @GetMapping("/ex01")
    public String ex01 (@AuthenticationPrincipal CustomUserDetails customUserDetails){
        System.out.println(customUserDetails.getRole());
        return "RBAC TEST";
    }
    // 리프레시 토큰 테스트
    @GetMapping("/ex02")
    public String ex02 (@AuthenticationPrincipal CustomUserDetails customUserDetails){
        System.out.println(customUserDetails.getRole());
        return "REFRESHTOKEN TEST";
    }
}
