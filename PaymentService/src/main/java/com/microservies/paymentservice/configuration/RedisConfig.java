package com.microservies.paymentservice.configuration;

import com.microservies.paymentservice.dtos.PaymentRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean("stringRedisTemplate")
    public StringRedisTemplate getStringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        StringRedisTemplate stringRedisTemplate=new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setEnableTransactionSupport(true);
        return stringRedisTemplate;
    }
    @Bean("objectRedisTemplate")
    public RedisTemplate<String,Object> objectRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(PaymentRequestDto.class));
        return  redisTemplate;
    }
}
