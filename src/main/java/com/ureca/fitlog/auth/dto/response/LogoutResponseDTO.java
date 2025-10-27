package com.ureca.fitlog.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogoutResponseDTO {
    private String message;

    public static LogoutResponseDTO of(String message) {
        return LogoutResponseDTO.builder()
                .message(message)
                .build();
    }
}