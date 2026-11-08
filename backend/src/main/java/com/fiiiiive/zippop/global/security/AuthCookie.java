package com.fiiiiive.zippop.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class AuthCookie {
    private AuthCookie() {
    }

    public static void add(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        boolean secure = request.isSecure() || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"));
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", secure ? "None" : "Lax");
        response.addCookie(cookie);
    }
}
