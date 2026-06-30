package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CajaCierreRequest {

    @NotNull(message = "El monto de cierre es obligatorio")
    @PositiveOrZero(message = "El monto de cierre no puede ser negativo")
    private Double montoCierre;

    @Size(max = 300, message = "Las observaciones no pueden superar 300 caracteres")
    private String observaciones;

    public Double getMontoCierre() { return montoCierre; }
    public void setMontoCierre(Double montoCierre) { this.montoCierre = montoCierre; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
