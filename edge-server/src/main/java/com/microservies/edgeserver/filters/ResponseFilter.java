package com.microservies.edgeserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ResponseFilter implements GlobalFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestFilter.class);
    private final FilterUtility filterUtility;

    @Autowired
    public ResponseFilter(FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(exchange.getResponse().getHeaders().get("X-Request-ID")==null){

        exchange.getResponse().getHeaders().add("X-Request-ID",filterUtility.getRequestId(exchange.getRequest().getHeaders()));
        }
        return chain.filter(exchange);
    }
}
