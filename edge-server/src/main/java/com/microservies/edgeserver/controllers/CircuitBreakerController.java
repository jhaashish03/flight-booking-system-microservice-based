package com.microservies.edgeserver.controllers;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/circuitBreaker")
public class CircuitBreakerController {

    @GetMapping("/something-went-wrong")
    public ResponseEntity<String> transactionCircuitBreaker() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE ).body("Something went wrong,please try again after some time");
    }
}
