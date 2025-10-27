package com.ureca.fitlog.profile.mapper;

import com.ureca.fitlog.profile.dto.request.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.response.ProfileResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfileMapper {

    /** 회원 프로필 조회 */
    ProfileResponseDTO findProfileByLoginId(@Param("loginId") String loginId);

    /** 회원 프로필 수정 */
    int updateProfile(@Param("loginId") String loginId,
                      @Param("request") ProfileRequestDTO request);
}
