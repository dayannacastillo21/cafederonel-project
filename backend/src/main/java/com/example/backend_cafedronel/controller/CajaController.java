package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.CajaAperturaRequest;
import com.example.backend_cafedronel.dto.CajaCierreRequest;
import com.example.backend_cafedronel.dto.CajaSesionResponse;
import com.example.backend_cafedronel.service.CajaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping("/activa")
    public ResponseEntity<CajaSesionResponse> activa(Authentication authentication) {
        CajaSesionResponse sesion = cajaService.obtenerActiva(emailDe(authentication));
        if (sesion == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/apertura")
    public ResponseEntity<CajaSesionResponse> abrir(
            @Valid @RequestBody CajaAperturaRequest request,
            Authentication authentication) {
        CajaSesionResponse sesion = cajaService.abrir(request, emailDe(authentication));
        return ResponseEntity.created(URI.create("/api/caja/" + sesion.getId())).body(sesion);
    }

    @PostMapping("/cierre")
    public ResponseEntity<CajaSesionResponse> cerrar(
            @Valid @RequestBody CajaCierreRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(cajaService.cerrar(request, emailDe(authentication)));
    }

    private String emailDe(Authentication authentication) {
        return authentication != null ? String.valueOf(authentication.getName()) : "";
    }
}
