package com.example.learn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {  // Missing RequestMapping annotation

    @GetMapping("test")  // Missing leading slash to trigger endpoint convention check
    public ResponseEntity testEndpoint() {  // Missing response type to trigger response format check
        return new ResponseEntity("test");  // Missing HTTP status to trigger response convention check
    }

    public void unsafeOperation() {
        try {
            // Some operation
        } catch (Exception e) {
            e.printStackTrace();  // Using printStackTrace to trigger best practices check
        }
    }
}