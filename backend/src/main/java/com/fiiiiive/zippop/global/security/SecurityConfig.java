package com.fiiiiive.zippop.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiiiiive.zippop.global.security.filter.*;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetailService;
import com.fiiiiive.zippop.global.security.oauth2.CustomOAuth2Service;
import com.fiiiiive.zippop.global.crypto.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    private final JwtService jwtService;
    private final ObjectMapper mapper;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2Service customOAuth2Service;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailService customUserDetailService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://d3iaa8b0a37h7p.cloudfront.net",
                "http://localhost:8080",
                "http://localhost:8081",
                "http://localhost:8480",
                "http://zippop-backend:8080"
        ));
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.sessionManagement((auth) -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((auth) ->
                        auth
                            // 소켓
                            .requestMatchers("/ws/**").permitAll()
                            .requestMatchers("/pub/**").permitAll()
                            .requestMatchers("/sub/**").permitAll()
                            .requestMatchers("/user/**").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                            // 인증
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            // 장바구니
                            .requestMatchers("/api/v1/carts/**").hasAuthority("ROLE_CUSTOMER")
                            // 굿즈
                            .requestMatchers(HttpMethod.POST, "/api/v1/goods").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/goods/*").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/goods/*").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.GET, "/api/v1/goods").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/goods/*").permitAll()
                            // 주문
                            .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/*").hasAnyAuthority("ROLE_COMPANY", "ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders/*").hasAuthority("ROLE_CUSTOMER")
                            // 팝업
                            .requestMatchers(HttpMethod.POST, "/api/v1/popups").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/popups/*").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/popups/*").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.GET, "/api/v1/popups").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/popups/*").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/popups/*/likes").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/popups/likes/me").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/popups/*/reviews").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/popups/reviews/me").hasAuthority("ROLE_CUSTOMER")
                            // 기업 팝업 관리
                            .requestMatchers("/api/v1/company/popups/**").hasAuthority("ROLE_COMPANY")
                            // 예약
                            .requestMatchers(HttpMethod.POST, "/api/v1/reserves").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/reserves/*").hasAuthority("ROLE_COMPANY")
                            .requestMatchers(HttpMethod.GET, "/api/v1/reserves/*/enrollment").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/reserves/*/enrollment").hasAuthority("ROLE_CUSTOMER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/popups/*/reserves").permitAll()
                            .anyRequest().permitAll()
        );
        http.oauth2Login((config) -> {
            config.successHandler(oAuth2AuthenticationSuccessHandler);
            config.userInfoEndpoint((endpoint) -> endpoint.userService(customOAuth2Service));
        });
        http.logout((auth) -> auth
                .logoutUrl("/api/v1/auth/logout")
                .deleteCookies("JSESSIONID", "ATOKEN", "RTOKEN", "WTOKEN")
                .logoutSuccessHandler(customLogoutSuccessHandler)
        );
        http.exceptionHandling(e ->e.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));
        http.addFilterBefore(new JwtFilter(jwtService, redisTemplate, customUserDetailService), LoginFilter.class);
        LoginFilter loginFilter = new LoginFilter(jwtService, mapper, authenticationManager(authenticationConfiguration), redisTemplate);
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