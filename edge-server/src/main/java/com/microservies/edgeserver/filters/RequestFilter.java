package com.microservies.edgeserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(1)
@Component
public class RequestFilter implements GlobalFilter {


    private static final Logger log = LoggerFactory.getLogger(RequestFilter.class);
    private final FilterUtility filterUtility;

    @Autowired
    public RequestFilter(FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
      HttpHeaders httpHeaders= exchange.getRequest().getHeaders();
      if(filterUtility.getRequestId(httpHeaders)==null){
          String requestId= UUID.randomUUID().toString();
          exchange.mutate().request(exchange.getRequest().mutate().header("X-Request-ID", requestId).build()).build();
      }
        MDC.put("X-Request-ID", exchange.getRequest().getHeaders().getFirst("X-Request-ID"));
        log.info("Request registered with id {}",filterUtility.getRequestId(httpHeaders));
      return chain.filter(exchange);
    }
}
