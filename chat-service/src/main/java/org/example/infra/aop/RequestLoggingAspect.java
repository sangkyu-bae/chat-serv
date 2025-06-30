package org.example.infra.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.modules.converter.JsonConverter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingAspect implements WebFilter {

    private final JsonConverter jsonFormatter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Body가 있는 메서드만 필터링 (POST, PUT, PATCH 등)
        if (!hasBody(originalRequest.getMethod().name())) {
            return chain.filter(exchange);
        }

        return DataBufferUtils.join(originalRequest.getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    String bodyString = new String(bytes, StandardCharsets.UTF_8);

                    // 로깅
                    log.info("[REQUEST BODY] {}", bodyString);

                    // 새로운 request로 교체 (한 번 소비된 body를 복구)
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(originalRequest) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                        }
                    };

                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                    return chain.filter(mutatedExchange);
                });
    }

    private boolean hasBody(String method) {
        return method.equalsIgnoreCase("POST")
                || method.equalsIgnoreCase("PUT")
                || method.equalsIgnoreCase("PATCH");
    }
}