package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // CONNECT 메시지에 대해서만 인증 처리
        if (accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }
        // WebSocket 세션에서 authToken 가져오기
        Map<String, Object> sessionAttributes = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        if (sessionAttributes == null || !sessionAttributes.containsKey("ATOKEN")) {
            log.warn("세션 속성이 없거나 Access Token이 없습니다.");
            return message;
        }
        String accessToken = (String) sessionAttributes.get("ATOKEN");
        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("Access Token이 비어있습니다.");
            return message;
        }
        try {
            // JWT에서 사용자 정보를 추출하여 CustomUserDetails 생성
            Long idx = jwtUtil.getIdx(accessToken);
            String email = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);
            String userId = jwtUtil.getUserId(accessToken);
            CustomUserDetails customUserDetails = new CustomUserDetails(idx, email, role, userId);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            accessor.setUser(authToken); // WebSocket 메시지의 Principal 설정
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("Security Context에 사용자 인증 설정 완료 - 사용자 ID: {}", userId);
        } catch (Exception e) {
            log.error("JWT를 이용한 사용자 정보 추출 중 오류 발생", e);
        }
        return message;
    }
}
