package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotBlank;

public class PedidoAdminPinRequest {

    @NotBlank(message = "El PIN de administrador es obligatorio")
    private String adminPin;

    public String getAdminPin() {
        return adminPin;
    }

    public void setAdminPin(String adminPin) {
        this.adminPin = adminPin;
    }
}
