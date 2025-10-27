package com.ureca.fitlog.profile.service;

import com.ureca.fitlog.common.exception.BusinessException;
import com.ureca.fitlog.common.exception.ExceptionStatus;
import com.ureca.fitlog.profile.dto.request.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.response.ProfileResponseDTO;
import com.ureca.fitlog.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileMapper profileMapper;

    /** 로그인 아이디로 회원 프로필 조회 + 나이 계산 */
    public ProfileResponseDTO getProfileByLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        ProfileResponseDTO profile = profileMapper.findProfileByLoginId(loginId);
        if (profile == null) {
            throw new BusinessException(ExceptionStatus.USER_DOMAIN_NOT_FOUND);
        }

        profile.calculateAgeFromBirthDate();
        return profile;
    }

    /** 프로필 수정 */
    @Transactional
    public ProfileResponseDTO updateProfile(String loginId, ProfileRequestDTO request) {
        if (loginId == null || loginId.isBlank()) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        int updated = profileMapper.updateProfile(loginId, request);
        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.USER_DOMAIN_NOT_FOUND);
        }

        return getProfileByLoginId(loginId);
    }
}