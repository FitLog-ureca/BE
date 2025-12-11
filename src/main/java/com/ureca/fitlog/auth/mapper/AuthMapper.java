package com.ureca.fitlog.auth.mapper;

import com.ureca.fitlog.auth.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;


@Mapper
public interface AuthMapper {

    void insertUser(@Param("name") String name,
                    @Param("birth") java.time.LocalDate birth,
                    @Param("loginId") String loginId,
                    @Param("password") String password);

    Long findLastInsertId();
    Long findUserIdByLoginId(@Param("loginId") String loginId);
    UserInfo findByLoginId(@Param("loginId") String loginId);

    // ID 중복 체크용 메서드 추가
    boolean existsByLoginId(@Param("loginId") String loginId);

    void updateRefreshToken(
            @Param("loginId") String loginId,
            @Param("refreshToken") String refreshToken,
            @Param("expire") LocalDateTime expire
    );
    String findRefreshTokenByLoginId(@Param("loginId") String loginId);

}
