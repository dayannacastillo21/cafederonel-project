package com.example.backend_cafedronel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiEstadoController {

    @GetMapping("/estado")
    public Map<String, Object> estado() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("aplicacion", "backend-cafedronel");
        body.put("ok", true);
        body.put("mensaje", "Backend conectado correctamente a Spring Boot y funcionando.");
        return body;
    }
}
