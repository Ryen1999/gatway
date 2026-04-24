package com.itmuch.gatway.filter;

import com.itmuch.gatway.security.jwt.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthGlobalFilter  implements GlobalFilter, Ordered {

    @Autowired
    private JwtOperator jwtOperator;

    // 不需要登录的白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/users/login", "/api/users/register",
            // 陪诊平台公开接口
            "/users/login",
            "/services",
            "/hospitals",
            "/escorts",
            "/banners",
            "/faqs"
    );

    /**
     * 登录认证
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        log.info("网关收到请求: {}", path);
        // 白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            log.info("白名单路径，直接放行: {}", path);
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("X-Token");
        if (token == null ) {
            log.info("请求头中X-Token为空");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            //  Claims claims = jwtOperator.getClaimsFromToken(token);
//            String userId = claims.get("id").toString();
//            String wxNickName = claims.get("wxNickName").toString();


            // 把用户ID注入请求头，下游服务直接用 @RequestHeader 读取
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                    .header("X-User-Id", userId)
//                    .header("X-wxNickName", wxNickName)
                    .header("X-Token", token)   // 原始 Token 也透传，供内部调用使用

                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
