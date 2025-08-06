package com.chat.chatgateway.filter;


import com.chat.chatgateway.jwt.JwtTokenProvider;
import com.chat.chatgateway.jwt.TokenService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenService tokenService;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");
            if (!jwtTokenProvider.validateJwtToken(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            Claims claims = jwtTokenProvider.getClaimsFromJwtToken(jwt);
            String userId = claims.getSubject();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder.header("X-User-Id", userId))
                    .request(builder -> builder.header("token", jwt))
                    .build();

            return tokenService.isBlackListToken(userId)
                    .flatMap(isBlackList ->{
                        if(isBlackList){
                            return onError(exchange, "JWT TOKEN in blackList", HttpStatus.UNAUTHORIZED);
                        }

                        return chain.filter(mutatedExchange);
                    })
                    .onErrorResume(e->{
                        return onError(mutatedExchange, "redis Error", HttpStatus.UNAUTHORIZED);
                    });
        };
    }
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }
}
