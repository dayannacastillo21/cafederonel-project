package com.example.backend_cafedronel.controller;

import com.example.backend_cafedronel.dto.PedidoAdminPinRequest;
import com.example.backend_cafedronel.dto.PedidoAdminUpdateRequest;
import com.example.backend_cafedronel.exception.ResourceNotFoundException;
import com.example.backend_cafedronel.mapper.PedidoMapper;
import com.example.backend_cafedronel.model.Pedido;
import com.example.backend_cafedronel.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/pedidos")
public class AdminPedidoController {

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/verificar-pin")
    public ResponseEntity<Void> verificarPin(@Valid @RequestBody PedidoAdminPinRequest request) {
        pedidoService.verificarPinAdmin(request.getAdminPin());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PedidoAdminUpdateRequest request) {
        Pedido actualizado = pedidoService.actualizarAdmin(id, PedidoMapper.toPedido(request), request.getAdminPin());
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(
            @PathVariable Integer id,
            @Valid @RequestBody PedidoAdminPinRequest request) {
        if (pedidoService.obtenerPorId(id).isEmpty()) {
            throw new ResourceNotFoundException("Pedido", id);
        }
        return ResponseEntity.ok(pedidoService.cancelarAdmin(id, request.getAdminPin()));
    }
}
