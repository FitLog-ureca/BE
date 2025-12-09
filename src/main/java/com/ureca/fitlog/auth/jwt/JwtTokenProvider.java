package com.ureca.fitlog.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;

    @Getter
    private final long accessTokenValidity;  // ms
    private final long refreshTokenValidity; // ms

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration}") long accessTokenValidity,
            @Value("${jwt.refresh-expiration}") long refreshTokenValidity
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /** JWT Access Token 생성 */
    public String createAccessToken(String loginId) {
        System.out.println("access token 발급 완료");
        return buildToken(loginId, accessTokenValidity);
    }

    /** refresh token 생성 */
    public String createRefreshToken(String loginId) {
        System.out.println("refresh token 발급 완료");
        return buildToken(loginId, refreshTokenValidity);
    }

    private String buildToken(String loginId, long validity) {
        Date now = new Date();
        Date expire =  new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰에서 사용자 아이디 추출 */
    public String getUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject(); // 만료된 토큰에서도 username은 추출 가능
        }
    }

    /** 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("JWT 만료됨: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("지원하지 않는 JWT 형식: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("JWT 구조가 올바르지 않음: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("JWT 서명 검증 실패: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 값이 비어있거나 잘못됨: " + e.getMessage());
        }

        return false;
    }

    /** 토큰 기반 Authentication 객체 생성 */
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}
