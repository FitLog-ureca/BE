package com.ureca.fitlog.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignupRequestDTO {
    private String name;
    private LocalDate birth;
    private String loginId;
    private String password;
    private String passwordCheck;
}
