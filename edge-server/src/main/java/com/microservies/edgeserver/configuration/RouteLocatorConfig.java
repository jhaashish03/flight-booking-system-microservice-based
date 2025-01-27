package com.microservies.edgeserver.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p->p
                        .path("/air-india/users/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath("/air-india/users/(?<segment>.*)","/${segment}")
                                        .addResponseHeader("X-Response-Time", new Date().toString())
                                .circuitBreaker(config -> config.setName("userServiceCircuitBreaker")
                                                .setFallbackUri("forward:/circuitBreaker/something-went-wrong"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()).setKeyResolver(keyResolver()))  )
                        .uri("lb://USER-SERVICE"))
                .route(p->p
                        .path("/air-india/flights/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath("/air-india/flights/(?<segment>.*)","/${segment}")
                                .addResponseHeader("X-Response-Time", new Date().toString())
                                .circuitBreaker(config -> config.setName("flightServiceCircuitBreaker")
                                        .setFallbackUri("forward:/circuitBreaker/something-went-wrong"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()).setKeyResolver(keyResolver())))
                        .uri("lb://FLIGHT-SERVICE"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(1,1,1);
    }

    @Bean
    public KeyResolver keyResolver(){
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getQueryParams().getFirst("user")).defaultIfEmpty("anonymous");
    }


    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(){

        return reactiveResilience4JCircuitBreakerFactoryCustomizer->reactiveResilience4JCircuitBreakerFactoryCustomizer.configureDefault(id->new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()).timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
    }
}
