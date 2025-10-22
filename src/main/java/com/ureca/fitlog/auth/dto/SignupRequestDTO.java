package com.ureca.fitlog.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {
    private String name;
    private String birth;
    private String id;
    private String password;
    private String passwordCheck;
}
