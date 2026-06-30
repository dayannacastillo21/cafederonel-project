package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DeduccionStockRequest {

    @NotNull(message = "Las unidades son obligatorias")
    @Positive(message = "Las unidades deben ser mayores que cero")
    private Integer unidades;

    public Integer getUnidades() {
        return unidades;
    }

    public void setUnidades(Integer unidades) {
        this.unidades = unidades;
    }
}
