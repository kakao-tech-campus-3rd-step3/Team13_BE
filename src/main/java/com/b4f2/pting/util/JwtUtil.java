package com.b4f2.pting.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public String createEmailToken(Member member, String schoolEmail) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 30 * 60 * 1000);

        return Jwts.builder()
            .claim("memberId", member.getId())
            .claim("schoolEmail", schoolEmail)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public String getSchoolEmail(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("schoolEmail", String.class);
    }

}
