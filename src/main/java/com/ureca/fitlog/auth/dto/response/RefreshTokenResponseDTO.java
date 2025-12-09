package com.ureca.fitlog.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshTokenResponseDTO {
    private String accessToken;
}
