package com.ureca.fitlog.profile.mapper;

import com.ureca.fitlog.profile.dto.ProfileResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProfileMapper {

    @Select("""
        SELECT 
            username AS name,             
            bio,
            profile_image AS profileImage,
            birth_date AS birthDate       
        FROM users
        WHERE login_id = #{loginId}
    """)
    ProfileResponseDTO findProfileByLoginId(String loginId);
}
