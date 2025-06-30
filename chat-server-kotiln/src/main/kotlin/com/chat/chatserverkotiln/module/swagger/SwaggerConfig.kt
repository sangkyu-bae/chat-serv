package com.chat.chatserverkotiln.module.swagger

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info // ✅ 어노테이션용
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Chat API",
        version = "v1",
        description = "WebFlux 기반 채팅 서비스 API",
        contact = Contact(name = "상규", email = "sanggyu@example.com")
    )
)
@Configuration
class SwaggerConfig {
}