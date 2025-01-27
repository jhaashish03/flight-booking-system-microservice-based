package com.ewallet.userservice.configurations.feignConfig;

import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
       return new FeignErrorDecoder();
   }

}
