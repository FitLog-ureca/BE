package com.ureca.fitlog.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Authorization 헤더에서 토큰 추출
        String token = extractAccessTokenFromHeader(request);

        // 2. 토큰 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // 3. 토큰이 없거나 만료된 경우, 쿠키의 refresh token으로 자동 갱신 시도
        else {
            String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookies(request);
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                try {
                    String loginId = jwtTokenProvider.getUsername(refreshToken);
                    String newAccessToken = jwtTokenProvider.createAccessToken(loginId);

                    // 새 토큰으로 인증 설정
                    Authentication auth = jwtTokenProvider.getAuthentication(newAccessToken);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // 응답 헤더에 새 토큰 전달 (프론트엔드에서 저장하도록)
                    response.setHeader("X-New-Access-Token", newAccessToken);
                } catch (Exception e) {
                    System.out.println("토큰 갱신 실패: " + e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    // 2. Authorization 헤더에서 Bearer 토큰 추출
    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
