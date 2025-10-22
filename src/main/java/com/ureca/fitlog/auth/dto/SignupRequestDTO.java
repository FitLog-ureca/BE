package com.ureca.fitlog.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {
    private String name;
    private String birth;
    private String loginId;
    private String password;
    private String passwordCheck;
}
