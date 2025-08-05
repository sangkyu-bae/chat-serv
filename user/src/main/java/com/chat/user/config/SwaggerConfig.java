package com.chat.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("채팅-유저 서비스 API 문서")
                        .description("채팅-유저 서비스 API 명세서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Chat User Service")
                                .email("admin@chat.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8092").description("Local Server")
                ));
    }
}
