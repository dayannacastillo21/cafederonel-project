package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.PosCheckoutRequest;
import com.example.backend_cafedronel.dto.PosCheckoutResponse;
import com.example.backend_cafedronel.service.PosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/pos")
public class PosController {

    private final PosService posService;

    public PosController(PosService posService) {
        this.posService = posService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<PosCheckoutResponse> checkout(
            @Valid @RequestBody PosCheckoutRequest request,
            Authentication authentication) {
        String email = authentication != null ? String.valueOf(authentication.getName()) : "";
        PosCheckoutResponse response = posService.checkout(request, email);
        return ResponseEntity.created(URI.create("/api/pedidos/" + response.getPedidoId())).body(response);
    }
}
