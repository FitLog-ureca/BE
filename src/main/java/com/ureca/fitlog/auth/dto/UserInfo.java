package com.ureca.fitlog.auth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private Long userId;
    private String username;
    private String password;
}