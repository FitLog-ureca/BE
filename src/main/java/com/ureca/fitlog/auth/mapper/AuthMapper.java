package com.ureca.fitlog.auth.mapper;

import com.ureca.fitlog.auth.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AuthMapper {

    void insertUser(@Param("name") String name,
                    @Param("birth") java.time.LocalDate birth,
                    @Param("loginId") String loginId,
                    @Param("password") String password);

    Long findLastInsertId();
    Long findUserIdByLoginId(@Param("loginId") String loginId);
    UserInfo findById(@Param("loginId") String loginId);
}
