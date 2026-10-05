package com.fiiiiive.zippop.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.crypto.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("RTOKEN".equals(cookie.getName()) && cookie.getValue() != null) {
                    try {
                        redisTemplate.delete("refreshToken:" + jwtService.getUserId(cookie.getValue()));
                    } catch (JwtException | IllegalArgumentException ignored) {
                        // Invalid refresh tokens are treated as already logged out.
                    }
                    break;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), new SuccessResponse<>(SuccessCode.AUTH_LOGOUT_SUCCESS));
        response.getWriter().flush();
    }

}
