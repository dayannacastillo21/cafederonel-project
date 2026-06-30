package com.example.backend_cafedronel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PedidoAdminUpdateRequest {

    @NotBlank(message = "El PIN de administrador es obligatorio")
    private String adminPin;

    @NotBlank(message = "El cliente es obligatorio")
    private String cliente;

    @NotEmpty(message = "El pedido debe incluir al menos una linea")
    @Valid
    private List<DetallePedidoLineRequest> detalles;

    public String getAdminPin() {
        return adminPin;
    }

    public void setAdminPin(String adminPin) {
        this.adminPin = adminPin;
    }

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
