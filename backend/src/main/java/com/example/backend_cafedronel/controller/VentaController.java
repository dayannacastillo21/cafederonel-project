package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.VentaRequest;
import com.example.backend_cafedronel.model.Producto;
import com.example.backend_cafedronel.model.Venta;
import com.example.backend_cafedronel.service.VentaService;
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
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(ventaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Venta>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ventaService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@Valid @RequestBody VentaRequest request) {
        Venta creada = ventaService.crear(toEntity(request));
        return ResponseEntity.created(URI.create("/api/ventas/" + creada.getId())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizar(@PathVariable Integer id, @Valid @RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.actualizar(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private static Venta toEntity(VentaRequest request) {
        Venta venta = new Venta();
        venta.setUsuarioId(request.getUsuarioId());
        venta.setCantidad(request.getCantidad());
        venta.setEstado(request.getEstado() != null && !request.getEstado().isBlank() ? request.getEstado() : "pendiente");
        venta.setMetodoPago(request.getMetodoPago());
        Producto ref = new Producto();
        ref.setId(request.getProductoId());
        venta.setProducto(ref);
        return venta;
    }
}
