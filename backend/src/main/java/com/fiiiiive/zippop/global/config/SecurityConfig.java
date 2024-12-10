package com.fiiiiive.zippop.global.config;

import com.fiiiiive.zippop.global.security.AccessControlService;
import com.fiiiiive.zippop.global.security.CustomUserDetailService;
import com.fiiiiive.zippop.global.security.filter.*;
import com.fiiiiive.zippop.global.security.oauth2.CustomOAuth2Service;
import com.fiiiiive.zippop.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2Service customOAuth2Service;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailService customUserDetailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AccessControlService accessControlService;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://d3iaa8b0a37h7p.cloudfront.net", "http://localhost:8081"));
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());
        http.sessionManagement((auth) -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilter(corsFilter());
        http.authorizeHttpRequests((auth) ->
                        auth
                            // 소켓
                            .requestMatchers("/ws/**").permitAll() // WebSocket 엔드포인트 허용
                            .requestMatchers("/pub/**").permitAll() // 메시지 매핑 허용
                            .requestMatchers("/sub/**").permitAll() // 메시지 브로커 허용
                            .requestMatchers("/user/**").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                            // 인증

                            .requestMatchers("/api/v1/auth/**").permitAll()
                            // 장바구니
                            .requestMatchers("/api/v1/cart/**").hasAuthority("ROLE_CUSTOMER")
                            // 굿즈
                            .requestMatchers("/api/v1/goods/register").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/goods/update").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/goods/delete").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/goods/search").permitAll()
                            .requestMatchers("/api/v1/goods/search-all").permitAll()
                            // 주문
                            .requestMatchers("/api/v1/orders/verify/stock").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/orders/verify/reserve").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/orders/cancel").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/orders/complete").hasAnyAuthority("ROLE_COMPANY", "ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/orders/search/as-customer").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/orders/search-all/as-customer").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/orders/search/as-company").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/orders/search-all/as-company").hasAuthority("ROLE_COMPANY")
                            // 팝업 스토어
                            .requestMatchers("/api/v1/store/register").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/store/update").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/store/delete").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/store/search").permitAll()
                            .requestMatchers("/api/v1/store/search-all").permitAll()
                            .requestMatchers("/api/v1/store/search-all/as-company").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/store/like/register").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/store/like/search-all").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/store/review/register").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/store/review/search-all").permitAll()
                            .requestMatchers("/api/v1/store/review/search-all/as-customer").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/auth/get-info").hasAnyAuthority("ROLE_COMPANY", "ROLE_CUSTOMER")
                            // 예약
                            .requestMatchers("/api/v1/reserve/register").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/reserve/enroll").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/reserve/cancel").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/reserve/status").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers("/api/v1/reserve/search-all/as-company").hasAuthority("ROLE_COMPANY")
                            .requestMatchers("/api/v1/reserve/search-all").permitAll()
                            .requestMatchers("/api/v1/reserve/access").access(accessControlService::hasReserveAccess)
                            .anyRequest().permitAll()
        );
        http.addFilter(corsFilter());
        http.oauth2Login((config) -> {
            config.successHandler(oAuth2AuthenticationSuccessHandler);
            config.userInfoEndpoint((endpoint) -> endpoint.userService(customOAuth2Service));
        });
        http.logout((auth) -> auth
                .logoutUrl("/api/v1/auth/logout")
                .deleteCookies("JSESSIONID", "ATOKEN", "RTOKEN")
                .logoutSuccessHandler(customLogoutSuccessHandler)
        );
        http.exceptionHandling(e ->e.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));
        http.addFilterBefore(new JwtFilter(jwtUtil, redisTemplate, customUserDetailService), LoginFilter.class);
        LoginFilter loginFilter = new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), redisTemplate);
        loginFilter.setFilterProcessesUrl("/api/v1/auth/login");
        loginFilter.setAuthenticationFailureHandler(customLoginFailureHandler);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }
}