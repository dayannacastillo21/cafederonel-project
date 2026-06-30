package com.example.backend_cafedronel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SecureController {
    @GetMapping("/admin/ping")
    public Map<String, Object> adminPing() { return Map.of("scope", "admin", "ok", true); }
    @GetMapping("/user/ping")
    public Map<String, Object> userPing() { return Map.of("scope", "user", "ok", true); }
}
