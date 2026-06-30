package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.UsuarioRegistroRequest;
import com.example.backend_cafedronel.dto.UsuarioResponse;
import com.example.backend_cafedronel.dto.UsuarioUpdateRequest;
import com.example.backend_cafedronel.model.Usuario;
import com.example.backend_cafedronel.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listar().stream()
                .map(UsuarioResponse::from)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegistroRequest request) {
        Usuario creado = usuarioService.registrar(request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId()))
                .body(UsuarioResponse.from(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateRequest request) {
        Usuario actualizado = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(UsuarioResponse.from(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
