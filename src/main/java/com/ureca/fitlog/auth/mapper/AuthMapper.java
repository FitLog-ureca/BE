package com.ureca.fitlog.auth.mapper;

import com.ureca.fitlog.auth.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AuthMapper {

    void insertUser(@Param("name") String name,
                    @Param("birth") String birth,
                    @Param("loginId") String loginId,
                    @Param("password") String password);

    Long findLastInsertId();

    UserInfo findById(@Param("loginId") String loginId);
}
