package com.chat.user.infra.api;

import com.chat.user.module.common.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@Hidden
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final JsonConverter jsonFormatter;
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // SpringDoc 관련 요청은 제외
        if (returnType.getContainingClass() != null) {
            String className = returnType.getContainingClass().getName();
            if (className.contains("springdoc") ||
                className.contains("swagger") ||
                className.contains("openapi")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String res = jsonFormatter.toJson(body);
        log.error(res);

        return body;
    }
}
