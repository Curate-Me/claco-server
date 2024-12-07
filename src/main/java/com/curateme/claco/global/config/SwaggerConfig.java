package com.curateme.claco.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        String jwtSchemeName = "JWT TOKEN";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
            .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        return new OpenAPI()
            .addServersItem(new Server().url("/"))
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(components);
    }

    private Info apiInfo() {
        return new Info()
            .title("Claco Springdoc")
            .description("Springdoc을 사용한 CLACO API 테스트")
            .version("1.0.0");
    }
}

