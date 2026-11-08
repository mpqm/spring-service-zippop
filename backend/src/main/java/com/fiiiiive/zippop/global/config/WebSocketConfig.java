package com.fiiiiive.zippop.global.config;
import com.fiiiiive.zippop.global.security.socket.CustomAuthenticationInterceptor;
import com.fiiiiive.zippop.global.security.socket.CustomHandshakeInterceptor;
import com.fiiiiive.zippop.reserve.infrastructure.stomp.ReserveStompInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomAuthenticationInterceptor customAuthenticationInterceptor;
    private final ReserveStompInterceptor reserveStompInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub", "/queue"); // 브로커 구독 경로
        config.setApplicationDestinationPrefixes("/pub"); // 발행 경로
        config.setUserDestinationPrefix("/user"); // 사용자 전용 경로 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "https://d3iaa8b0a37h7p.cloudfront.net",
                    "http://localhost:8080",
                    "http://localhost:8081",
                    "http://localhost:8480"
                )
                .addInterceptors(new CustomHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(customAuthenticationInterceptor, reserveStompInterceptor);
    }

}
