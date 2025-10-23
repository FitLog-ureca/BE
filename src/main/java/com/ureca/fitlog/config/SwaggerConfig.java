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
        // 쿠키 기반 인증 스키마 정의
        String cookieAuthName = "cookieAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("FitLog API")
                        .version("1.0")
                        .description("FitLog API 문서입니다. 로그인 후 쿠키를 통해 인증됩니다."))
                .addSecurityItem(new SecurityRequirement().addList(cookieAuthName))
                .components(new Components()
                        .addSecuritySchemes(cookieAuthName, new SecurityScheme()
                                .name("accessToken") // 쿠키 이름
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("로그인 후 자동으로 설정되는 JWT 쿠키입니다.")));
    }
}