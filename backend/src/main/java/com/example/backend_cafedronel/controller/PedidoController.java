package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.PedidoClienteUpdateRequest;
import com.example.backend_cafedronel.dto.PedidoCreateRequest;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.mapper.PedidoMapper;
import com.example.backend_cafedronel.model.DetallePedido;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.service.DetallePedidoService;
import com.example.backend_cafedronel.service.PedidoService;
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
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final DetallePedidoService detallePedidoService;

    public PedidoController(PedidoService pedidoService, DetallePedidoService detallePedidoService) {
        this.pedidoService = pedidoService;
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Integer id) {
        Pedido pedido = pedidoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetallePedido>> detalles(@PathVariable Integer id) {
        if (pedidoService.obtenerPorId(id).isEmpty()) {
            throw new ResourceNotFoundException("Pedido", id);
        }
        return ResponseEntity.ok(detallePedidoService.obtenerPorPedido(id));
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoCreateRequest request) {
        Pedido creado = pedidoService.crear(PedidoMapper.toPedido(request));
        return ResponseEntity.created(URI.create("/api/pedidos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Integer id, @Valid @RequestBody PedidoCreateRequest request) {
        Pedido actualizado = pedidoService.actualizar(id, PedidoMapper.toPedido(request));
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Integer id, @RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    @PatchMapping("/{id}/cliente")
    public ResponseEntity<Pedido> actualizarCliente(
            @PathVariable Integer id,
            @Valid @RequestBody PedidoClienteUpdateRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarCliente(id, request.getCliente()));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
