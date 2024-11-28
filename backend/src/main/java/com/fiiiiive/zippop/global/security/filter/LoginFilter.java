package com.fiiiiive.zippop.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.global.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import com.fiiiiive.zippop.auth.model.dto.PostLoginReq;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        PostLoginReq dto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            dto = objectMapper.readValue(messageBody, PostLoginReq.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getPassword(), null);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails member = (CustomUserDetails)authResult.getPrincipal();
        Long idx = member.getIdx();
        String email = member.getEmail();
        String role = member.getRole();
        String userId = member.getUserId();

        // 새로운 accessToken, refreshToken 발급
        String accessToken = jwtUtil.createAccessToken(idx, email, role, userId);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken);

        // accessToken, refreshToken, userToken 쿠키 설정
        Cookie aToken = new Cookie("ATOKEN", accessToken);
        
        aToken.setHttpOnly(true);
        aToken.setSecure(true);
        aToken.setPath("/");
        aToken.setAttribute("SamSite", "None");
        response.addCookie(aToken);

        Cookie rToken = new Cookie("RTOKEN", refreshToken);
        rToken.setHttpOnly(true);
        rToken.setSecure(true);
        rToken.setPath("/");
        aToken.setAttribute("SamSite", "None");
        response.addCookie(rToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{"
                        + "\"success\": true,"
                        + "\"code\": 2030,"
                        + "\"message\": \"로그인에 성공했습니다.\","
                        + "\"result\": null"
                + "}"
        );
        response.getWriter().flush();
    }
}
