package com.ureca.fitlog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI fitlogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitLog API 문서")
                        .description("FitLog API 명세서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("URECA Team")
                                .email("ureca@fitlog.com")));
    }
}
