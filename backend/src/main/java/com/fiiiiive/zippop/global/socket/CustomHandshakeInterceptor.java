package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


import org.springframework.http.server.ServletServerHttpRequest;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // TCP 연결을 하기전에 요청을 가로챔
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // 쿠키에서 인증 정보를 추출하여 WebSocket 세션에 저장
            String accessToken = null;
            Cookie[] cookies = servletRequest.getCookies();
            
            // 쿠키가 null이 아닐 때만 처리
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("ATOKEN".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        if (accessToken != null) {
                            attributes.put("ATOKEN", accessToken);
                        }
                    }
                }
            }

            // accessToken이 있을 때만 인증 처리
            if (accessToken != null) {
                Long idx = jwtService.getIdx(accessToken);
                String email = jwtService.getUsername(accessToken);
                String role = jwtService.getRole(accessToken);
                String userId = jwtService.getUserId(accessToken);
                CustomUserDetails customUserDetails = CustomUserDetails.builder()
                        .idx(idx)
                        .email(email)
                        .role(role)
                        .userId(userId)
                        .build();
                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("WebSocket 연결 차단: 쿠키에서 ATOKEN을 찾을 수 없습니다.");
                return false; // 인증 실패 시 연결 차단
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {}

}
