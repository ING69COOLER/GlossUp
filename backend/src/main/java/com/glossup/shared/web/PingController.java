package com.glossup.shared.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

/** Endpoint publico de verificacion rapida de que la aplicacion esta viva. */
@RestController
public class PingController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "service", "glossup-backend",
                "status", "UP",
                "timestamp", OffsetDateTime.now().toString());
    }
}
