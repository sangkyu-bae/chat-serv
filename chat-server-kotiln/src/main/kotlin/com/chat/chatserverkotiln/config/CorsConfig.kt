package com.chat.chatserverkotiln.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class CorsConfig : WebFluxConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")          // 모든 경로에 대해
            .allowedOrigins("*")            // 모든 출처 허용
            .allowedMethods("*")            // 모든 HTTP 메서드 허용(GET, POST, 등)
            .allowedHeaders("*")            // 모든 헤더 허용
            .allowCredentials(false)        // 자격증명 허용 안 함 (true로 설정 시 allowedOrigins에 '*' 못 씀)
            .maxAge(3600)                   // preflight 캐시 시간 (초)
    }
}