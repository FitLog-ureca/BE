package com.ureca.fitlog.auth.controller;

import com.ureca.fitlog.auth.dto.request.LoginRequestDTO;
import com.ureca.fitlog.auth.dto.request.SignupRequestDTO;
import com.ureca.fitlog.auth.dto.response.LoginResponseDTO;
import com.ureca.fitlog.auth.dto.response.LogoutResponseDTO;
import com.ureca.fitlog.auth.dto.response.RefreshTokenResponseDTO;
import com.ureca.fitlog.auth.dto.response.SignupResponseDTO;
import com.ureca.fitlog.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "회원가입 및 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    /** 회원가입 */
    @PostMapping("/signup")
    @Operation(
            summary = "회원가입"
    )
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        SignupResponseDTO response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 로그인 (JWT 발급 포함) */
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        LoginResponseDTO loginResponse = authService.login(request);

        // refresh token을 쿠키로 내려보냄
        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshCookie);

        // access token만 body로 전달 (refreshToken 제외)
        return ResponseEntity.ok(
                LoginResponseDTO.builder()
                        .message(loginResponse.getMessage())
                        .loginId(loginResponse.getLoginId())
                        .name(loginResponse.getName())
                        .accessToken(loginResponse.getAccessToken())
                        .build()
        );
    }

    /** 로그아웃 (쿠키 삭제) */
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃"
    )
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {
        // service에서 DB의 refresh token 삭제
        LogoutResponseDTO res = authService.logout(request);

        // refresh token 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: 배포시 true로 변경 필수
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(res);
    }

    /** 리프레시 토큰 요청 */
    @PostMapping("/refresh")
    @Operation(summary = "리프레시 토큰 요청")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(HttpServletRequest request) {
        RefreshTokenResponseDTO response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }
}
