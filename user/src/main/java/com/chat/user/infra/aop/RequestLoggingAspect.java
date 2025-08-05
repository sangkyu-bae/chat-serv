package com.chat.user.infra.aop;


import com.chat.user.module.common.converter.JsonConverter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    private final JsonConverter jsonFormatter;

    public RequestLoggingAspect(JsonConverter jsonFormatter) {
        this.jsonFormatter = jsonFormatter;
    }

    @Around("execution(* com.chat..*Controller.*(..))")
    public Object logRequestBody(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : joinPoint.getArgs()) {
            if (arg != null
                    && !(arg instanceof ServletRequest)
                    && !(arg instanceof ServletResponse)
                    && !(arg instanceof BindingResult)) {

                try {
                    String json = jsonFormatter.toJson(arg);
                    log.info("RequestBody: {}", json);
                } catch (Exception e) {
                    log.warn("Failed to serialize arg: {}", arg.getClass().getName(), e);
                }
            }
        }


        return joinPoint.proceed();
    }

    private boolean isRequestBody(Object arg) {
        // 원하는 조건에 따라 필터링 가능 (예: DTO 클래스 이름 패턴 등)
//        return !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof Errors);
        return !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse );
    }
}