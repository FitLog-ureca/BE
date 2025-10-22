package com.ureca.fitlog.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String loginId;
    private String password;
}
