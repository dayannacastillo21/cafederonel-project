package com.example.backend_cafedronel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PedidoCreateRequest {

    @NotBlank(message = "El cliente es obligatorio")
    private String cliente;

    @NotEmpty(message = "El pedido debe incluir al menos una línea")
    @Valid
    private List<DetallePedidoLineRequest> detalles;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public List<DetallePedidoLineRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoLineRequest> detalles) {
        this.detalles = detalles;
    }
}
