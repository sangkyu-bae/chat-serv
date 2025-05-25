package org.example.infra.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.JsonConverter;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    private final JsonConverter jsonFormatter;

    public RequestLoggingAspect(JsonConverter jsonFormatter) {
        this.jsonFormatter = jsonFormatter;
    }

    @Around("execution(* org.example..*Controller.*(..))")
    public Object logRequestBody(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg != null && isRequestBody(arg)) {
                String json = jsonFormatter.toJson(arg);
                log.info("[REQUEST BODY] {}", json);
            }
        }

        return joinPoint.proceed();
    }

    private boolean isRequestBody(Object arg) {
        return !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse );
    }
}