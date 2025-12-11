package com.ureca.fitlog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Bearer JWT 인증 스키마 정의
        String jwtSchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("FitLog API")
                        .version("1.0")
                        .description("FitLog API 문서입니다. 로그인 후 Authorize 버튼을 클릭하여 JWT 토큰을 입력하세요."))
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("JWT 토큰을 입력하세요 (Bearer prefix 없이)")));
    }
}