package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.DeduccionStockRequest;
import com.example.backend_cafedronel.dto.EstadoRequest;
import com.example.backend_cafedronel.dto.InventarioRequest;
import com.example.backend_cafedronel.dto.MovimientoInventarioRequest;
import com.example.backend_cafedronel.model.Inventario;
import com.example.backend_cafedronel.model.MovimientoInventario;
import com.example.backend_cafedronel.service.InventarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> listar(@RequestParam(name = "q", required = false) String termino) {
        return ResponseEntity.ok(inventarioService.buscar(termino));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoInventario>> listarUltimosMovimientos() {
        return ResponseEntity.ok(inventarioService.listarUltimosMovimientos());
    }

    @GetMapping("/alertas/stock-bajo")
    public ResponseEntity<List<Inventario>> listarStockBajo() {
        return ResponseEntity.ok(inventarioService.listarConStockBajo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@Valid @RequestBody InventarioRequest request) {
        Inventario creado = inventarioService.crear(toEntity(request));
        return ResponseEntity.created(URI.create("/api/inventario/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Integer id, @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(id, toEntity(request)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Inventario> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoRequest request) {
        Inventario actualizado = inventarioService.cambiarEstado(id, Boolean.TRUE.equals(request.getActivo()));
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deducciones")
    public ResponseEntity<Inventario> deducirStock(@PathVariable Integer id, @Valid @RequestBody DeduccionStockRequest request) {
        return ResponseEntity.ok(inventarioService.deducirStock(id, request.getUnidades()));
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoInventario>> listarMovimientos(@PathVariable Integer id) {
        return ResponseEntity.ok(inventarioService.listarMovimientos(id));
    }

    @PostMapping("/{id}/movimientos")
    public ResponseEntity<MovimientoInventario> registrarMovimiento(
            @PathVariable Integer id,
            @Valid @RequestBody MovimientoInventarioRequest request) {
        MovimientoInventario movimiento = inventarioService.registrarMovimiento(id, request);
        return ResponseEntity.created(URI.create("/api/inventario/" + id + "/movimientos/" + movimiento.getId())).body(movimiento);
    }

    private static Inventario toEntity(InventarioRequest request) {
        Inventario item = new Inventario();
        item.setNombreInsumo(request.getNombreInsumo());
        item.setCodigoInsumo(request.getCodigoInsumo());
        item.setCategoria(request.getCategoria());
        item.setUbicacion(request.getUbicacion());
        item.setAlmacenId(request.getAlmacenId());
        item.setLote(request.getLote());
        item.setFechaVencimiento(request.getFechaVencimiento());
        item.setCantidad(request.getCantidad());
        item.setUnidad(request.getUnidad());
        item.setStockMinimo(request.getStockMinimo());
        item.setPrecioUnitario(request.getPrecioUnitario());
        item.setProveedor(request.getProveedor());
        item.setActivo(request.getActivo());
        return item;
    }
}
