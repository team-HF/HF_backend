package com.hf.healthfriend.domain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Sample {

    @GetMapping("/pro")
    public String get() {
        return "ok";
    }
}
