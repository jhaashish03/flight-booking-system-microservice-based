package com.microservies.edgeserver.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class FilterUtility {

    public String getRequestId(HttpHeaders httpHeaders){
        return httpHeaders.getFirst("X-Request-ID");
    }
}
