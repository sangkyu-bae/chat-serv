package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.modules.converter.JsonConverter;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogWebFilter implements WebFilter {

    private final JsonConverter jsonFormatter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String method = request.getMethod().name();
        String uri = request.getURI().getPath();
        String userId = "teest"; // 이후 인증 정보에서 추출 가능

        String transactionId = UUID.randomUUID().toString();
        String threadId = String.valueOf(Thread.currentThread().getId());

        MDC.put("transactionId", transactionId);
        MDC.put("threadId", threadId);

        Map<String, List<String>> paramMap = request.getQueryParams();
        Map<String, Object> formattedParams = paramMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size() == 1 ? entry.getValue().get(0) : entry.getValue()
                ));

        String params = jsonFormatter.toJson(formattedParams);

        if (method.equals("GET")) {
            log.info("[start] : -> {}가 {} 로 조회 요청하였습니다", userId, uri);
        } else if (method.equals("POST")) {
            log.info("[start] : -> {}가 {}로 {}정보의 등록 요청하였습니다", userId, uri, params);
        } else if (method.equals("DELETE")) {
            log.info("[start] : -> {}가 {}로 삭제 요청하였습니다", userId, uri);
        } else if (method.equals("PUT")) {
            log.info("[start] :  -> {}가 {}로 {}정보 수정 요청하였습니다", userId, uri, params);
        }

        return chain.filter(exchange)
                .doFinally(signalType -> MDC.clear()); // 요청 끝나면 로그 컨텍스트 정리
    }
}