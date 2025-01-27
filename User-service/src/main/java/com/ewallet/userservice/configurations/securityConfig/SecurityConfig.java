package com.ewallet.userservice.configurations.securityConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.version}")
    private String version;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(requests->requests.
            requestMatchers("/"+version+"/users/**").hasAnyAuthority("USER")
                    .requestMatchers("/"+version+"/admins/**").hasAnyAuthority("ADMIN")
                    .requestMatchers("/public").permitAll()
                    .requestMatchers("/"+version+"/accounts/**").permitAll().anyRequest().authenticated())
            .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                    .loginPage("/"+version+"/accounts/login")
                    .loginProcessingUrl("/perform_login")
//                    .successForwardUrl("/account/login/success")
                    .defaultSuccessUrl("/"+version+"/accounts/login/success", true)
                    .failureUrl("/login?error=true"));

    return httpSecurity.build();
    }
}
