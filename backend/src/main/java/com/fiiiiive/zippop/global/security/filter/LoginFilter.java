package com.fiiiiive.zippop.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiiiiive.zippop.account.model.dto.LoginReq;
import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
        LoginReq req;
        try {
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            req = mapper.readValue(messageBody, LoginReq.class);
            
            // 아이디와 비밀번호 입력 검증
            if (req.getUserId() == null || req.getUserId().trim().isEmpty()) {
                throw new BadCredentialsException(ServiceErrorCode.AUTH_LOGIN_FAIL_ID_NULL.getMessage());
            }
            if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
                throw new BadCredentialsException(ServiceErrorCode.AUTH_LOGIN_FAIL_PASSWORD_NULL.getMessage());
            }
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.getUserId(), req.getPassword(), null);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            ServerException serverException = new ServerException(ServerErrorCode.MALFORMED_JSON, e);
            throw new InternalAuthenticationServiceException(serverException.getMessage(), serverException);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
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
        SuccessResponse<Void> successResponse = new SuccessResponse<>(SuccessCode.AUTH_LOGIN_SUCCESS);
        mapper.writeValue(response.getWriter(), successResponse);
        response.getWriter().flush();
    }

}
