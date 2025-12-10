package com.ureca.fitlog.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDTO {
    private String message;
    private String accessToken;
}
