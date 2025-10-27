package com.ureca.fitlog.auth.service;

import com.ureca.fitlog.auth.dto.*;
import com.ureca.fitlog.auth.dto.request.LoginRequestDTO;
import com.ureca.fitlog.auth.dto.request.SignupRequestDTO;
import com.ureca.fitlog.auth.dto.response.LoginResponseDTO;
import com.ureca.fitlog.auth.dto.response.LogoutResponseDTO;
import com.ureca.fitlog.auth.dto.response.SignupResponseDTO;
import com.ureca.fitlog.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;

    /** 회원가입 */
    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO request) {
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // DB 저장 (현재는 평문으로 저장) -> 나중에 bycr? 로 해서 암호화
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
        UserInfo user = authMapper.findById(request.getLoginId());

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
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
