package com.ureca.fitlog.auth.controller;

import com.ureca.fitlog.auth.dto.*;
import com.ureca.fitlog.auth.jwt.JwtTokenProvider;
import com.ureca.fitlog.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        try {
            LoginResponseDTO loginResult = authService.login(request);
            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(loginResult.getLoginId());
            Cookie cookie = new Cookie("accessToken", token);
            cookie.setHttpOnly(true);  // JS 접근 불가
            cookie.setSecure(true);    // HTTPS 전용
            cookie.setPath("/");       // 모든 경로에 유효
            cookie.setMaxAge(60 * 60); // 1시간 (AccessToken 유효기간과 동일)
            response.addCookie(cookie);


            // Builder로 새 객체 생성
            LoginResponseDTO responseBody = LoginResponseDTO.builder()
                    .message("로그인에 성공했습니다.")
                    .loginId(loginResult.getLoginId())
                    .name(loginResult.getName())
                    .token(token)
                    .build();

            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDTO.builder()
                            .message("아이디 또는 비밀번호가 올바르지 않습니다.")
                            .build());
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponseDTO.builder()
                            .message("로그인 처리 중 오류가 발생했습니다.")
                            .build());
        }
    }
    /** 로그아웃 (쿠키 삭제) */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        LogoutResponseDTO res = LogoutResponseDTO.of("로그아웃 되었습니다.");
        return ResponseEntity.ok(res);
    }
}
