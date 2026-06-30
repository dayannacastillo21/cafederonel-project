package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotNull;

public class EstadoRequest {

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
