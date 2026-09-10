package com.azaaza.habitpet.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * 발급/검증을 한곳에 모은 JWT 유틸.
 * 토큰 payload에는 userId(subject)만 넣는다 — 이메일/닉네임처럼 바뀔 수 있는 값을
 * 토큰에 실으면 갱신 전까지 stale한 정보를 신뢰하게 되는 문제가 있어서 최소한만 담았다.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    /**
     * 검증 + subject 추출을 한 번의 파싱으로 끝낸다. 예전엔 isValid()와 getUserId()가
     * 별도 메서드라 필터가 둘 다 호출했는데, 그러면 같은 토큰의 서명을 요청마다 두 번
     * 검증하는 꼴이었다 — 인증된 모든 요청에서 HMAC 검증 비용이 두 배로 드는 셈이라 합쳤다.
     */
    public Optional<Long> resolveUserId(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.of(Long.valueOf(claims.getSubject()));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
