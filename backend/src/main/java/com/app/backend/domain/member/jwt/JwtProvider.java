package com.app.backend.domain.member.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1일

    // access token 생성
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .claim("sub", username) // 회원 ID도 저장 추가
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // refresh token 생성
    public String generateRefreshToken() {
        return Jwts.builder()
                .expiration(Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRE_TIME)))
                .signWith(getSigningKey())
                .compact();
    }

    // 토큰 유효성 검증
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
        // 1. 토큰에서 username(subject) 추출
        String username = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        // 2. UserDetails 객체 생성
        UserDetails userDetails = User.builder()
                .username(username)
                .password("") // 토큰 기반 인증이므로 비밀번호는 불필요
                .roles("USER") // 기본 역할 설정
                .build();

        // 3. Authentication 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}
