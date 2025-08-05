package com.chat.user.config;

import com.chat.user.config.interceptor.LogInterceptor;
import com.chat.user.module.common.converter.JsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor(new JsonConverter(new ObjectMapper())))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/css/**", "/js/**");
    }
}
