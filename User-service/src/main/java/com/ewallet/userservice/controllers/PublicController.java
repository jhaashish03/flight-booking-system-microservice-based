package com.ewallet.userservice.controllers;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final Environment env;

    public PublicController(Environment env) {
        this.env = env;
    }

    @GetMapping
    public ResponseEntity<String> getMessage(){
        return ResponseEntity.ok(env.getProperty("info.developerName"));
    }
}
