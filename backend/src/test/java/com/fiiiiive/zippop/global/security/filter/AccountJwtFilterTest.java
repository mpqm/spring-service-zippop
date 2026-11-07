package com.fiiiiive.zippop.global.security.filter;

import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.*;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.concurrent.TimeUnit;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountJwtFilterTest {
    private static final String SECRET = "01234567890123456789012345678901";
    private final JwtService jwt = new JwtService(SECRET);
    private final CustomUserDetailService users = mock(CustomUserDetailService.class);
    @SuppressWarnings("unchecked")
    private final RedisTemplate<String,Object> redis = mock(RedisTemplate.class);
    private final JwtFilter filter = new JwtFilter(jwt, redis, users);

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    @Test void validAccessTokenLoadsTheAccountAndSeparateCustomerProfile() throws Exception {
        var user = user(true, 0);
        when(users.loadByAccountId(99L)).thenReturn(user);
        perform("ATOKEN", jwt.createAccessToken(user));
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
        assertThat(user.getCustomerId()).isEqualTo(3L);
    }
    @Test void disabledAccountCannotUsePreviouslyIssuedAccessToken() throws Exception {
        when(users.loadByAccountId(99L)).thenReturn(user(false, 1));
        perform("ATOKEN", jwt.createAccessToken(user(true, 0)));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    @Test void passwordChangeRejectsEarlierTokenVersion() throws Exception {
        when(users.loadByAccountId(99L)).thenReturn(user(true, 1));
        perform("ATOKEN", jwt.createAccessToken(user(true, 0)));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    @Test void refreshTokenCannotBeUsedAsAccessToken() throws Exception {
        perform("ATOKEN", jwt.createRefreshToken(user(true, 0)));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(users);
    }
    @Test void malformedTokenDoesNotThrowServerError() throws Exception {
        MockHttpServletRequest request = perform("ATOKEN", "invalid-token");
        assertThat(request.getAttribute("exception")).isInstanceOf(Exception.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    @Test void oldProfileIdTokensAreNotInterpretedAsAccountIds() throws Exception {
        String old = Jwts.builder().claim("idx", 99L).claim("role", "ROLE_CUSTOMER")
                .expiration(new Date(System.currentTimeMillis()+60000))
                .signWith(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256")).compact();
        perform("ATOKEN", old);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(users);
    }
    @Test void disabledAccountCannotRefreshItsSession() throws Exception {
        when(users.loadByAccountId(99L)).thenReturn(user(false, 1));
        perform("RTOKEN", jwt.createRefreshToken(user(true, 0)));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(redis);
    }
    @SuppressWarnings("unchecked")
    @Test void refreshUsesTheUnversionedAccountKeyForLookupAndRotation() throws Exception {
        var user = user(true, 0);
        String refresh = jwt.createRefreshToken(user);
        ValueOperations<String, Object> values = mock(ValueOperations.class);
        when(users.loadByAccountId(99L)).thenReturn(user);
        when(redis.opsForValue()).thenReturn(values);
        when(values.get("refreshToken:99")).thenReturn(refresh);

        perform("RTOKEN", refresh);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
        verify(values).get("refreshToken:99");
        verify(values).set(eq("refreshToken:99"), anyString(), eq(5L), eq(TimeUnit.DAYS));
    }

    private MockHttpServletRequest perform(String cookie, String token) throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/v1/accounts/me");
        request.setCookies(new Cookie(cookie, token));
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        return request;
    }
    private CustomUserDetails user(boolean enabled, long version) {
        return CustomUserDetails.builder().accountId(99L).customerId(3L).role("ROLE_CUSTOMER")
                .email("member@example.test").enabled(enabled).authVersion(version).build();
    }
}
