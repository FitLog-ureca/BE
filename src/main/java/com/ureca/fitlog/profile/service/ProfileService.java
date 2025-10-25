package com.ureca.fitlog.profile.service;

import com.ureca.fitlog.profile.dto.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.ProfileResponseDTO;
import com.ureca.fitlog.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileMapper profileMapper;

    /** 로그인 아이디로 회원 프로필 조회 + 나이 계산 */
    public ProfileResponseDTO getProfileByLoginId(String loginId) {
        ProfileResponseDTO profile = profileMapper.findProfileByLoginId(loginId);
        if (profile != null) {
            profile.calculateAgeFromBirthDate();
        }
        return profile;
    }

    /** 프로필 수정 */
    public ProfileResponseDTO updateProfile(String loginId, ProfileRequestDTO request) {
        int updated = profileMapper.updateProfile(loginId, request);
        if (updated > 0) {
            return getProfileByLoginId(loginId);
        }
        return null;
    }
}
