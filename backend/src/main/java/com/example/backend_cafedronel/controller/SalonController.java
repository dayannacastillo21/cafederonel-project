package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.SalonMesaResponse;
import com.example.backend_cafedronel.service.SalonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salon")
public class SalonController {

    private final SalonService salonService;

    public SalonController(SalonService salonService) {
        this.salonService = salonService;
    }

    @GetMapping("/mesas")
    public ResponseEntity<List<SalonMesaResponse>> listarMesas() {
        return ResponseEntity.ok(salonService.listarMesas());
    }

    @GetMapping("/mesas/{numero}")
    public ResponseEntity<SalonMesaResponse> obtenerMesa(@PathVariable Integer numero) {
        return ResponseEntity.ok(salonService.obtenerMesa(numero));
    }

    @PostMapping("/mesas/{numero}/cuenta")
    public ResponseEntity<SalonMesaResponse> marcarCuenta(@PathVariable Integer numero) {
        return ResponseEntity.ok(salonService.marcarCuenta(numero));
    }

    @PostMapping("/mesas/{numero}/liberar")
    public ResponseEntity<SalonMesaResponse> liberarMesa(@PathVariable Integer numero) {
        return ResponseEntity.ok(salonService.liberarMesa(numero));
    }
}
