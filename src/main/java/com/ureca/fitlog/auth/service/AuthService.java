package com.ureca.fitlog.auth.service;

import com.ureca.fitlog.auth.dto.*;
import com.ureca.fitlog.auth.dto.request.LoginRequestDTO;
import com.ureca.fitlog.auth.dto.request.SignupRequestDTO;
import com.ureca.fitlog.auth.dto.response.LoginResponseDTO;
import com.ureca.fitlog.auth.dto.response.LogoutResponseDTO;
import com.ureca.fitlog.auth.dto.response.RefreshTokenResponseDTO;
import com.ureca.fitlog.auth.dto.response.SignupResponseDTO;
import com.ureca.fitlog.auth.jwt.JwtTokenProvider;
import com.ureca.fitlog.auth.mapper.AuthMapper;
import com.ureca.fitlog.common.exception.BusinessException;
import com.ureca.fitlog.common.exception.ExceptionStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /** 회원가입 */
    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO request) {
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BusinessException(ExceptionStatus.AUTH_SIGNUP_PASSWORD_MISMATCH);
        }

        // 2. 아이디 중복 체크
        if (authMapper.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ExceptionStatus.AUTH_SIGNUP_DUPLICATE_LOGIN_ID);
        }

        // DB 저장
        authMapper.insertUser(
                request.getName(),
                request.getBirth(),
                request.getLoginId(),
                request.getPassword()
        );

        // 새로 삽입된 user_id 조회
        Long userId = authMapper.findLastInsertId();

        // 응답 데이터 반환
        return SignupResponseDTO.builder()
                .message("회원가입이 완료되었습니다.")
                .userId(userId)
                .name(request.getName())
                .loginId(request.getLoginId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /** 로그인 */
    public LoginResponseDTO login(LoginRequestDTO request) {
        UserInfo user = authMapper.findByLoginId(request.getLoginId());

        if (user == null) {
            throw new BusinessException(ExceptionStatus.AUTH_LOGIN_USER_NOT_FOUND);
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ExceptionStatus.AUTH_LOGIN_INVALID_PASSWORD);
        }

        // AccessToken + RefreshToken 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId());

        // refreshToken DB 저장
        authMapper.updateRefreshToken(
                user.getLoginId(),
                refreshToken,
                LocalDateTime.now().plusDays(7)
        );

        return LoginResponseDTO.builder()
                .message("로그인에 성공했습니다.")
                .loginId(user.getLoginId())
                .name(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /** 로그아웃 - DB의 Refresh Token 삭제 */
    @Transactional
    public LogoutResponseDTO logout(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookies(request);

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String loginId = jwtTokenProvider.getUsername(refreshToken);
            // DB에서 Refresh Token 삭제
            authMapper.updateRefreshToken(loginId, null, null);
        }

        return LogoutResponseDTO.builder()
                .message("로그아웃이 완료되었습니다.")
                .build();
    }

    /** refresh 를 통한 access token 재발급 */
    public RefreshTokenResponseDTO refreshAccessToken(HttpServletRequest request) {

        // 1. 쿠키에서 Refresh Token 추출
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new BusinessException(ExceptionStatus.AUTH_TOKEN_REFRESH_TOKEN_NOT_FOUND);
        }

        // 2. refreshToken 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ExceptionStatus.AUTH_TOKEN_REFRESH_TOKEN_INVALID);
        }

        String loginId = jwtTokenProvider.getUsername(refreshToken);

        // 3. DB에 저장된 토큰과 일치 여부 확인
        String storedToken = authMapper.findRefreshTokenByLoginId(loginId);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(ExceptionStatus.AUTH_TOKEN_REFRESH_TOKEN_MISMATCH);
        }

        // 4. 새 AccessToken 발급 및 DTO 반환
        return RefreshTokenResponseDTO.builder()
                .message("토큰 재발급에 성공했습니다.")
                .accessToken(jwtTokenProvider.createAccessToken(loginId))
                .build();
    }


}
