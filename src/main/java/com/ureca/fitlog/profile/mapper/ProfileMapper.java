package com.ureca.fitlog.profile.mapper;

import com.ureca.fitlog.profile.dto.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.ProfileResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProfileMapper {

    /** 회원 프로필 조회 */
    ProfileResponseDTO findProfileByLoginId(@Param("loginId") String loginId);

    /** 회원 프로필 수정 */
    int updateProfile(@Param("loginId") String loginId,
                      @Param("request") ProfileRequestDTO request);
}
