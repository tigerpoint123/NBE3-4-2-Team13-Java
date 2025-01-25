package com.app.backend.domain.member.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1일

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .claim("sub", username)
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .expiration(Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRE_TIME)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Jwts.SIG     // JJWT 의 서명 기능 사용
                .HS256      // HMAC(대칭키 암호화 알고리즘. 256비트 보안 강도) SHA-256 알고리즘 사용
                .key()       // 키 생성
                .build();  // 자동으로 안전한 키 생성
    }

    public Authentication getAuthentication(String token) {
        return null;
    }
}
