package com.fiiiiive.zippop.global.security.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiiiiive.zippop.global.crypto.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CustomLogoutSuccessHandlerTest {

    private static final String SECRET = "01234567890123456789012345678901";

    @Test
    void logoutDeletesTheStoredRefreshTokenAndUsesTheSharedSuccessContract() throws Exception {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtService jwtService = new JwtService(SECRET);
        CustomLogoutSuccessHandler handler = new CustomLogoutSuccessHandler(redisTemplate, jwtService, objectMapper);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("RTOKEN", jwtService.createRefreshToken("customer01")));
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onLogoutSuccess(request, response, null);

        verify(redisTemplate).delete("refreshToken:customer01");
        JsonNode body = objectMapper.readTree(response.getContentAsString(StandardCharsets.UTF_8));
        assertThat(body.get("success").asBoolean()).isTrue();
        assertThat(body.get("code").asInt()).isEqualTo(1006);
    }

    @Test
    void reserveTokenLifetimeMatchesTheTenMinuteCookieLifetime() throws Exception {
        JwtService jwtService = new JwtService(SECRET);
        String token = jwtService.createReserveToken(9L, "user@test.com");
        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        JsonNode claims = new ObjectMapper().readTree(payload);

        assertThat(claims.get("exp").asLong() - claims.get("iat").asLong()).isEqualTo(600L);
    }
}
