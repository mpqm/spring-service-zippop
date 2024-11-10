package com.fiiiiive.zippop.global.config;

import com.fiiiiive.zippop.global.security.CustomUserDetailService;
import com.fiiiiive.zippop.global.security.filter.*;
import com.fiiiiive.zippop.global.security.oauth2.CustomOAuth2Service;
import com.fiiiiive.zippop.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
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
//                            .requestMatchers("/api/v1/test/**").authenticated()
                            .requestMatchers("/api/v1/test/ex01").hasAuthority("ROLE_COMPANY") //RBAC 테스트
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