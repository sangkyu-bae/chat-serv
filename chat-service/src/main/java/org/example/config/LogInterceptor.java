package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.module.converter.JsonConverter;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final JsonConverter jsonFormatter;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = "teest";
        String method = request.getMethod();
        String uri = request.getRequestURI();
;
        Map<String, Object> formattedParams = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String[] values = entry.getValue();
                            return values.length == 1 ? values[0] : Arrays.asList(values);
                        }
                ));

        String params = jsonFormatter.toJson(formattedParams);

        String transactionId = UUID.randomUUID().toString();
        String threadId = String.valueOf(Thread.currentThread().getId());
        
        MDC.put("transactionId", transactionId);
        MDC.put("threadId", threadId);

        if(method.equals("GET")){
            log.info("[start] : -> {}가 {} 로 조회 요청하였습니다",userId,uri);
        }else if(method.equals("POST")){
            log.info("[start] : -> {}가 {}로 {}정보의 등록 요청하였습니다",userId,uri);
            log.info(params);
        }else if (method.equals("DELETE")) {
            log.info("[start] : -> {}가 {}로 삭제 요청하였습니다",userId,uri);
        }else if (method.equals("PUT")){
            log.info("[start] :  -> {}가 {}로 {}정보 수정 요청하였습니다",userId,uri,params);
        }

        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String userId = "teest";
//        String method = request.getMethod();
//        String uri = request.getRequestURI();
//        ObjectMapper objectMapper = new ObjectMapper();
//        String params = objectMapper.writeValueAsString(request.getParameterMap());
//
//        if(method.equals("GET")){
//            log.info("{}가 {} 로 조회 요청하였습니다",userId,uri);
//        }else if(method.equals("POST")){
//            log.info("{}가 {}로 {}정보의 등록 요청하였습니다",userId,uri,params);
//        }else if (method.equals("DELETE")) {
//            log.info("{}가 {}로 삭제 요청하였습니다",userId,uri);
//        }else if (method.equals("PUT")){
//            log.info("{}가 {}로 {}정보 수정 요청하였습니다",userId,uri,params);
//        }
        MDC.clear();
    }
}
