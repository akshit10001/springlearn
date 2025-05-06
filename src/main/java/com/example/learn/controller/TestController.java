package com.example.learn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/status")
    public ResponseEntity<String> testEndpoint() {
        return new ResponseEntity<>("Service is running", HttpStatus.OK);
    }

    public void safeOperation() {
        try {
            // Some operation
        } catch (Exception e) {
            logger.error("Operation failed", e);
        }
    }
}