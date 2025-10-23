package com.ureca.fitlog.auth.controller;

import com.ureca.fitlog.auth.dto.*;
import com.ureca.fitlog.auth.jwt.JwtTokenProvider;
import com.ureca.fitlog.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /** 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        SignupResponseDTO response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 로그인 (JWT 발급 포함) */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        // AuthService에서 이미 LoginResponseDTO 리턴하도록 구성되어 있다면
        LoginResponseDTO response = authService.login(request);
        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(response.getLoginId());
        // 토큰 추가 후 반환
        response.setToken(token);
        return ResponseEntity.ok(response);
    }
    /** 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout() {
        LogoutResponseDTO response = authService.logout();
        return ResponseEntity.ok(response);
    }
}
