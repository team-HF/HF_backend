package com.hf.healthfriend.global.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hf")
public class BlueGreenController {

    @Value("${DEPLOY_ENVIRONMENT}")
    private String env;

    @GetMapping("/current-state")
    public String getCurrentState() {
        return this.env;
    }
}
