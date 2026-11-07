package com.fiiiiive.zippop.global.security.filter;

import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailService users;

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String access = cookie(request, "ATOKEN"), refresh = cookie(request, "RTOKEN");
        try {
            if (access != null && !jwtService.isExpired(access)) {
                authenticate(verifiedUser(access, "access"));
            } else if (refresh != null && !jwtService.isExpired(refresh)) {
                CustomUserDetails user = verifiedUser(refresh, "refresh");
                String key = "refreshToken:" + user.getAccountId();
                if (!refresh.equals(redisTemplate.opsForValue().get(key)))
                    throw new JwtException("다시 로그인해주세요.");
                String newRefresh = jwtService.createRefreshToken(user);
                redisTemplate.opsForValue().set(key, newRefresh, 5, TimeUnit.DAYS);
                tokenCookie(response, "ATOKEN", jwtService.createAccessToken(user));
                tokenCookie(response, "RTOKEN", newRefresh);
                authenticate(user);
            }
        } catch (JwtException | IllegalArgumentException | AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", exception);
        }
        chain.doFilter(request, response);
    }
    private CustomUserDetails verifiedUser(String token, String type) {
        CustomUserDetails user = users.loadByAccountId(jwtService.getAccountId(token, type));
        if (!user.isEnabled()) throw new DisabledException("비활성화된 계정입니다.");
        if (user.getAuthVersion() != jwtService.getAuthVersion(token))
            throw new JwtException("계정 인증 정보가 변경되었습니다.");
        return user;
    }
    private void authenticate(CustomUserDetails user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }
    private String cookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null)
            for (Cookie cookie : request.getCookies()) if (name.equals(cookie.getName())) return cookie.getValue();
        return null;
    }
    private void tokenCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); cookie.setSecure(true); cookie.setPath("/");
        cookie.setAttribute("SameSite", "None"); response.addCookie(cookie);
    }
}
