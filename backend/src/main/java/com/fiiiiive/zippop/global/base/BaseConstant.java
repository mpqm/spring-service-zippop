package com.fiiiiive.zippop.global.base;


public class BaseConstant {

    // 로그인 실패 시 리다이렉션 URL
    public static final String LOGIN_ERROR_REDIRECT_URL = "http://localhost:8081/login?error=true";
    // 로그인 성공 시 리다이렉션 URL
    public static final String LOGIN_SUCCESS_REDIRECT_URL = "http://localhost:8081/login?success=true";

    public static final String DEFAULT_SERVER_URL = "http://localhost:8080";

    private BaseConstant() {
        // 인스턴스 생성 방지
    }
}
