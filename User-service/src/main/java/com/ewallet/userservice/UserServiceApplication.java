package com.ewallet.userservice;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(
        info = @Info(
                title = "User Service REST API Doc",
                version = "1.1",
                description = "Rest Api doc for User service E-wallet application",
                contact = @Contact(
                        name = "Ashish Jha",
                        url = "https://hcltech.com",
                        email = "jhaashish123456789@gmail.com"
                )
        )
)@EnableAsync
@EnableRedisIndexedHttpSession(maxInactiveIntervalInSeconds=86400)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
