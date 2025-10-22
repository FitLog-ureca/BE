package com.ureca.fitlog.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SignupResponseDTO {
    private Long userId;
    private String name;
    private String id;
    private LocalDateTime createdAt;
}
