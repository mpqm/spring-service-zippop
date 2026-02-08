package com.fiiiiive.zippop.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;
    private final ObjectMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AccountDto.LoginReq dto;
        try {
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            dto = mapper.readValue(messageBody, AccountDto.LoginReq.class);
            
            // 아이디와 비밀번호 입력 검증
            if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
                throw new BadCredentialsException(BaseMessage.AUTH_LOGIN_FAIL_ID_NULL.getMessage());
            }
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                throw new BadCredentialsException(BaseMessage.AUTH_LOGIN_FAIL_PASSWORD_NULL.getMessage());
            }
            
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
        String accessToken = jwtService.createAccessToken(idx, email, role, userId);
        String refreshToken = jwtService.createRefreshToken(userId);

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken);

        // accessToken, refreshToken, userToken 쿠키 설정
        Cookie aToken = new Cookie("ATOKEN", accessToken);

        aToken.setHttpOnly(true);
        aToken.setSecure(true);
        aToken.setPath("/");
        aToken.setAttribute("SameSite", "None");
        response.addCookie(aToken);

        Cookie rToken = new Cookie("RTOKEN", refreshToken);
        rToken.setHttpOnly(true);
        rToken.setSecure(true);
        rToken.setPath("/");
        rToken.setAttribute("SameSite", "None");
        response.addCookie(rToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        BaseResponse<Void> baseResponse = new BaseResponse<>(BaseMessage.AUTH_LOGIN_SUCCESS);
        mapper.writeValue(response.getWriter(), baseResponse);
        response.getWriter().flush();
    }

}
