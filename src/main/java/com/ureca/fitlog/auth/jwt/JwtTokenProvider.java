package com.ureca.fitlog.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long validityInMilliseconds
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /** JWT 토큰 생성 */
    public String createToken(String loginId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // 설정값 기반 만료시간

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(validity)
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
        // 권한 정보가 있다면 여기에 roles 파싱 추가 가능
//        return new UsernamePasswordAuthenticationToken(username, "", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        return new UsernamePasswordAuthenticationToken(
                username,
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}
