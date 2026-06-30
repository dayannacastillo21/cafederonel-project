package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CajaAperturaRequest {

    @NotNull(message = "El monto inicial es obligatorio")
    @PositiveOrZero(message = "El monto inicial no puede ser negativo")
    private Double montoInicial;

    public Double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(Double montoInicial) { this.montoInicial = montoInicial; }
}
