package com.fiiiiive.zippop.global.security.filter;
import com.fiiiiive.zippop.global.security.CustomUserDetailService;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.global.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws  ServletException, IOException, InternalAuthenticationServiceException {
        String accessToken = null;
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("ATOKEN")) {
                    accessToken = cookie.getValue();
                }
                if (cookie.getName().equals("RTOKEN")) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        // 처음 로그인 시
        if (accessToken == null && refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // Access Token 만료 여부 확인
        if (accessToken == null || jwtUtil.isExpired(accessToken)) {
            // Refresh Token이 존재하고 유효한 경우(저장된 Refresh Token과 일치하면) Access Token과 Refresh Token 재발급
            if (refreshToken == null || jwtUtil.isExpired(refreshToken) || !validateRefreshToken(refreshToken)) {
                filterChain.doFilter(request, response);
                return;
            }
            String email = jwtUtil.getEmail(refreshToken);
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(email);
            String newAccessToken = jwtUtil.createAccessToken(userDetails.getIdx(), userDetails.getEmail(), userDetails.getRole());
            String newRefreshToken = jwtUtil.createRefreshToken(userDetails.getEmail());
            redisTemplate.opsForValue().set("refreshToken:" + email, newRefreshToken);
            setTokenCookie(response, "ATOKEN", newAccessToken);
            setTokenCookie(response, "RTOKEN", newRefreshToken);
            // 새로운 Access Token을 기반으로 인증 정보 설정
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        // Access Token이 유효한 경우
        } else {
            try {
                Long idx = jwtUtil.getIdx(accessToken);
                String email = jwtUtil.getUsername(accessToken);
                String role = jwtUtil.getRole(accessToken);
                CustomUserDetails customUserDetails = new CustomUserDetails(idx, email, role);
                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException | UsernameNotFoundException e) {
                request.setAttribute("exception", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    // 쿠키 설정
    private void setTokenCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    // Refresh Token 검증
    private boolean validateRefreshToken(String refreshToken) {
        String storedRefreshToken = (String) redisTemplate.opsForValue().get("refreshToken:" + jwtUtil.getEmail(refreshToken));
        if(storedRefreshToken != null && storedRefreshToken.equals(refreshToken)){
            return true;
        }else{
            return false;
        }
    }
}
