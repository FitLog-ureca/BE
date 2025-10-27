package com.ureca.fitlog.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SignupResponseDTO {
    private String message;
    private Long userId;
    private String name;
    private String loginId;
    private LocalDateTime createdAt;
}
