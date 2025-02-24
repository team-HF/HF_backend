package com.hf.healthfriend.global.deployment;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/hf")
@Profile("prod")
@Slf4j
public class BlueGreenController {
    
    @Value("${deployment.current-mode}")
    private BlueGreen currentMode;

    @PostConstruct
    public void init() {
        log.info("CURRENT MODE: {}", this.currentMode);
    }

    @GetMapping("/current-mode")
    public ResponseEntity<String> getCurrentMode() {
        return ResponseEntity.ok(this.currentMode.name());
    }
}
