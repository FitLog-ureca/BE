package com.ureca.fitlog.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String message;
    private String loginId;
    private String name;
}
