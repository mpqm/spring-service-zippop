package com.fiiiiive.zippop.global.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private SecretKey secretKey;

    // JwtUtil 생성자
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    // 엑세스 토큰 생성
    public String createAccessToken(Long id, String username, String role) {
        return Jwts.builder()
                .claim("idx", id)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 1)) // 1시간 1000 * 60 * 60 * 1
                .signWith(secretKey)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5 )) // 5일 1000 * 60 * 60 * 24 * 5
                .signWith(secretKey)
                .compact();
    }

    // 예약 토큰 생성
    public String createReserveToken(Long reserveIdx, String email) {
        return Jwts.builder()
                .claim("reserveIdx", reserveIdx)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 100000))
                .signWith(secretKey)
                .compact();
    }

    // 멤버 인덱스 조회
    public Long getIdx(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("idx", Long.class);
    }

    // 멤버 이름 조회
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 멤버 역할 조회
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // 멤버 이메일 조회
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    // 토큰 만료 시간 확인
    public Boolean isExpired(String token) {
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException expiredJwtException){
            return true;
        }
    }


}
