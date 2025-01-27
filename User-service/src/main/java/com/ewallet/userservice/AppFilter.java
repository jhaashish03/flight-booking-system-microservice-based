package com.ewallet.userservice;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class AppFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
      log.info( SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
    chain.doFilter(request, response);
    }
}
