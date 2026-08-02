package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    public static final String SESSION_AUTHENTICATION = "AUTHENTICATION";

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication != null
                && currentAuthentication.isAuthenticated()
                && currentAuthentication.getPrincipal() instanceof CustomUserDetails) {
            attributes.put(SESSION_AUTHENTICATION, currentAuthentication);
            return true;
        }

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set("X-Error-Code", ServerErrorCode.AUTHENTICATION_REQUIRED.name());
            return false;
        }

        String accessToken = null;
        Cookie[] cookies = servletRequest.getServletRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ATOKEN".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        if (accessToken == null || accessToken.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set("X-Error-Code", ServerErrorCode.AUTHENTICATION_REQUIRED.name());
            return false;
        }

        try {
            CustomUserDetails user = CustomUserDetails.builder()
                    .idx(jwtService.getIdx(accessToken))
                    .email(jwtService.getUsername(accessToken))
                    .role(jwtService.getRole(accessToken))
                    .userId(jwtService.getUserId(accessToken))
                    .build();
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
            attributes.put(SESSION_AUTHENTICATION, authentication);
            return true;
        } catch (ExpiredJwtException exception) {
            log.debug("WebSocket access token expired");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set("X-Error-Code", ServerErrorCode.TOKEN_EXPIRED.name());
            return false;
        } catch (JwtException | IllegalArgumentException exception) {
            log.debug("WebSocket authentication failed: {}", exception.getClass().getSimpleName());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set("X-Error-Code", ServerErrorCode.INVALID_TOKEN.name());
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // no-op
    }
}
