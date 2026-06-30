package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.EstadoRequest;
import com.example.backend_cafedronel.dto.ProveedorRequest;
import com.example.backend_cafedronel.model.Proveedor;
import com.example.backend_cafedronel.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {
        return ResponseEntity.ok(proveedorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(@Valid @RequestBody ProveedorRequest request) {
        Proveedor creado = proveedorService.crear(toEntity(request));
        return ResponseEntity.created(URI.create("/api/proveedores/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Integer id, @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizar(id, toEntity(request)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Proveedor> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoRequest request) {
        Proveedor actualizado = proveedorService.cambiarEstado(id, Boolean.TRUE.equals(request.getActivo()));
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private static Proveedor toEntity(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        proveedor.setCodigoProveedor(request.getCodigoProveedor());
        proveedor.setNombre(request.getNombre());
        proveedor.setCategoria(request.getCategoria());
        proveedor.setRuc(request.getRuc());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setCiudad(request.getCiudad());
        proveedor.setEmail(request.getEmail());
        proveedor.setSitioWeb(request.getSitioWeb());
        proveedor.setNotas(request.getNotas());
        proveedor.setActivo(request.isActivo());
        return proveedor;
    }
}
