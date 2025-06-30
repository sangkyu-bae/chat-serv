package org.example.infra.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.modules.converter.JsonConverter;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
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
public class ResponseAdvice implements WebFilter {

    private final JsonConverter jsonFormatter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.doOnNext(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        
                        String responseBody = new String(bytes, StandardCharsets.UTF_8);
                        log.info("[RESPONSE BODY] {}", responseBody);
                        
                        // 새로운 DataBuffer로 교체
                        DataBuffer newBuffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        super.writeWith(Flux.just(newBuffer));
                    }));
                }
                return super.writeWith(body);
            }
        };

        ServerWebExchange mutatedExchange = exchange.mutate().response(decoratedResponse).build();
        return chain.filter(mutatedExchange);
    }
}
