package com.ureca.fitlog.auth.service;

import com.ureca.fitlog.auth.dto.SignupRequestDTO;
import com.ureca.fitlog.auth.dto.SignupResponseDTO;
import com.ureca.fitlog.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;

    /** 회원가입 */
    public SignupResponseDTO signup(SignupRequestDTO request) {
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // DB 저장 (현재는 평문으로 저장) -> 나중에 bycr? 로 해서 암호화
        authMapper.insertUser(
                request.getName(),
                request.getBirth(),
                request.getId(),
                request.getPassword()
        );

        // 새로 삽입된 user_id 조회
        Long userId = authMapper.findLastInsertId();

        // 응답 데이터 반환
        return SignupResponseDTO.builder()
//                .status("success")
//                .message("회원가입이 완료되었습니다.")
                .userId(userId)
                .name(request.getName())
                .id(request.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
