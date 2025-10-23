package com.ureca.fitlog.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * JWT 인증 필터에서 등록된 Authentication 객체에서
 * 현재 로그인한 사용자의 loginId를 추출하는 유틸리티
 */
public final class SecurityUtil {

    private SecurityUtil() {} // 인스턴스화 방지

    public static String getLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return authentication.getName(); // JwtAuthenticationFilter에서 set한 loginId
    }
}
