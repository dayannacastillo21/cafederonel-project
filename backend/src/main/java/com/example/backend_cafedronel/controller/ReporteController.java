package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.ResumenFinancieroResponse;
import com.example.backend_cafedronel.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen-financiero")
    public ResponseEntity<ResumenFinancieroResponse> resumenFinanciero() {
        return ResponseEntity.ok(reporteService.resumenFinanciero());
    }
}
