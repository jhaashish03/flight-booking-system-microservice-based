package com.ewallet.userservice.configurations.redisConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
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

        GenericJackson2JsonRedisSerializer objectJackson2JsonRedisSerializer=new GenericJackson2JsonRedisSerializer();
        objectJackson2JsonRedisSerializer.configure(objectMapper ->
                objectMapper.registerModule(new JavaTimeModule()));


        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(objectJackson2JsonRedisSerializer);
        return  redisTemplate;
    }



    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        return objectMapper;
    }
    }

