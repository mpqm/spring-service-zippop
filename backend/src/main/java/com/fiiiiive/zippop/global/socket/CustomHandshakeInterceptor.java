package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    public static final String SESSION_AUTHENTICATION = "AUTHENTICATION";
    @Override public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler handler, Map<String, Object> attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user
                && user.isEnabled()) {
            attributes.put(SESSION_AUTHENTICATION, auth);
            return true;
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set("X-Error-Code", ServerErrorCode.AUTHENTICATION_REQUIRED.name());
        return false;
    }
    @Override public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler handler, Exception exception) { }
}
