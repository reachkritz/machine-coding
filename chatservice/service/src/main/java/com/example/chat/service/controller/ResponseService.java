package com.example.chat.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResponseService {

    @GetMapping("/response")
    public String getResponse() {
        return "Response";
    }
    
}
