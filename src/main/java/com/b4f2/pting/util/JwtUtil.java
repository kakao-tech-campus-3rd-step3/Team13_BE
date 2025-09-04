package com.b4f2.pting.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.b4f2.pting.domain.Member;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${auth.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Member member) {
        return Jwts.builder()
            .claim("memberId", member.getId())
            .signWith(secretKey)
            .compact();
    }

    public Long getMemberId(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("memberId", Long.class);
    }
}
