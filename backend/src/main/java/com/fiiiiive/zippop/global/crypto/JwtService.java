package com.fiiiiive.zippop.global.crypto;

import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {
    private final SecretKey secretKey;
    public JwtService(@Value("${spring.security.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
    public String createAccessToken(CustomUserDetails user) { return accountToken(user, "access", 3600000L); }
    public String createRefreshToken(CustomUserDetails user) { return accountToken(user, "refresh", 432000000L); }
    private String accountToken(CustomUserDetails user, String type, long lifetime) {
        return Jwts.builder().subject(user.getAccountId().toString()).id(UUID.randomUUID().toString())
                .claim("version", 2).claim("tokenType", type).claim("authVersion", user.getAuthVersion())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + lifetime))
                .signWith(secretKey).compact();
    }
    public String createReserveToken(Long reserveIdx, String email) {
        return Jwts.builder().claim("reserveIdx", reserveIdx).claim("email", email)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 600000L))
                .signWith(secretKey).compact();
    }
    private Claims claims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
    public Long getAccountId(String token, String type) {
        Claims claims = claims(token);
        if (!Integer.valueOf(2).equals(claims.get("version", Integer.class))
                || !type.equals(claims.get("tokenType", String.class)) || claims.getSubject() == null)
            throw new UnsupportedJwtException("다시 로그인해주세요.");
        return Long.valueOf(claims.getSubject());
    }
    public long getAuthVersion(String token) { return claims(token).get("authVersion", Long.class); }
    public Boolean isExpired(String token) {
        try { return claims(token).getExpiration().before(new Date()); }
        catch (ExpiredJwtException exception) { return true; }
    }
}
