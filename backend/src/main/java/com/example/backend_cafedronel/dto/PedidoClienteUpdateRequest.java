package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotBlank;

public class PedidoClienteUpdateRequest {

    @NotBlank(message = "El cliente o destino es obligatorio")
    private String cliente;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
