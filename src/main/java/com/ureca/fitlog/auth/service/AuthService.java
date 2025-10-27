package com.ureca.fitlog.auth.service;

import com.ureca.fitlog.auth.dto.*;
import com.ureca.fitlog.auth.dto.request.LoginRequestDTO;
import com.ureca.fitlog.auth.dto.request.SignupRequestDTO;
import com.ureca.fitlog.auth.dto.response.LoginResponseDTO;
import com.ureca.fitlog.auth.dto.response.LogoutResponseDTO;
import com.ureca.fitlog.auth.dto.response.SignupResponseDTO;
import com.ureca.fitlog.auth.mapper.AuthMapper;
import com.ureca.fitlog.common.exception.BusinessException;
import com.ureca.fitlog.common.exception.ExceptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;

    /** 회원가입 */
    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO request) {
        // 입력값 유효성 검증
        if (!StringUtils.hasText(request.getLoginId()) ||
                !StringUtils.hasText(request.getPassword()) ||
                !StringUtils.hasText(request.getName())) {
            throw new BusinessException(ExceptionStatus.AUTH_SIGNUP_INVALID_INPUT);
        }

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BusinessException(ExceptionStatus.AUTH_SIGNUP_PASSWORD_MISMATCH);
        }

        try {
            // DB 저장 (현재는 평문으로 저장) -> 나중에 BCrypt로 암호화
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
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionStatus.AUTH_SIGNUP_DUPLICATE_LOGIN_ID);
        } catch (Exception e) {
            throw new BusinessException(ExceptionStatus.SERVER_DATABASE_ERROR);
        }
    }

    /** 로그인 */
    public LoginResponseDTO login(LoginRequestDTO request) {
        // 입력값 유효성 검증
        if (!StringUtils.hasText(request.getLoginId()) ||
                !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ExceptionStatus.AUTH_LOGIN_INVALID_CREDENTIALS);
        }
        // 사용자 조회
        UserInfo user = authMapper.findById(request.getLoginId());

        if (user == null) {
            throw new BusinessException(ExceptionStatus.AUTH_LOGIN_USER_NOT_FOUND);
        }

        // 비밀번호 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ExceptionStatus.AUTH_LOGIN_INVALID_PASSWORD);
        }

        return LoginResponseDTO.builder()
                .message("로그인에 성공했습니다.")
                .loginId(user.getLoginId())
                .name(user.getUsername())
                .build();
    }
    /** 로그아웃 */
    public LogoutResponseDTO logout() {
        return LogoutResponseDTO.builder()
                .message("로그아웃이 완료되었습니다.")
                .build();
    }
}
