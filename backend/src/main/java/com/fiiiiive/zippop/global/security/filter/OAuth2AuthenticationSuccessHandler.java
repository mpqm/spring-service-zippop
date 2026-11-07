package com.fiiiiive.zippop.global.security.filter;

import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.security.oauth2.CustomOauth2UserDetails;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component @RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redis;
    @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        var user = ((CustomOauth2UserDetails) authentication.getPrincipal()).getAccount();
        String refresh = jwtService.createRefreshToken(user);
        redis.opsForValue().set("refreshToken:" + user.getAccountId(), refresh, 5, TimeUnit.DAYS);
        addCookie(response, "ATOKEN", jwtService.createAccessToken(user));
        addCookie(response, "RTOKEN", refresh);
        getRedirectStrategy().sendRedirect(request, response,
                ServletUriComponentsBuilder.fromContextPath(request).path("/").build().toUriString());
    }
    private void addCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true); cookie.setSecure(true); cookie.setPath("/");
        cookie.setAttribute("SameSite", "None"); response.addCookie(cookie);
    }
}
