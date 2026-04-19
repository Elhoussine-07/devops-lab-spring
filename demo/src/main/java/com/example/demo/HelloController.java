package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${HOSTNAME:unknown}")
    private String hostname;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot - Instance: " + hostname;
    }
}