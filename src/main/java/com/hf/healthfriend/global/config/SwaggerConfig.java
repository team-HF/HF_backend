package com.hf.healthfriend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        Info info = new Info()
                .title("Health Friend API")
                .description("""
                        Health Friend API 문서입니다.
                        궁금한 점이 있으면 지체없이 말씀해 주세요.
                        """)
                .version("v0.0.1");

        List<Server> servers = List.of(new Server().url("http://localhost:8080")); // TODO: Environment Variable로 처리

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("Opaque Token")
                .in(SecurityScheme.In.HEADER);

        Components components = new Components()
                .addSecuritySchemes("OAuth 2.0 Bearer Auth", securityScheme);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        return new OpenAPI()
                .info(info)
                .servers(servers)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
