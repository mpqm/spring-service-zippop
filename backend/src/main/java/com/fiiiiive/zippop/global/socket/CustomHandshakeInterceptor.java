package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.global.utils.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // TCP 연결을 하기전에 요청을 가로챔
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // 쿠키에서 인증 정보를 추출하여 WebSocket 세션에 저장
            String accessToken = null;
            for (Cookie cookie : servletRequest.getCookies()) {
                if ("ATOKEN".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    if (accessToken != null) {
                        attributes.put("ATOKEN", accessToken);

                    }
                }
            }
            Long idx = jwtUtil.getIdx(accessToken);
            String email = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);
            String userId = jwtUtil.getUserId(accessToken);
            CustomUserDetails customUserDetails = CustomUserDetails.builder()
                    .idx(idx)
                    .email(email)
                    .role(role)
                    .userId(userId)
                    .build();
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {}
}
