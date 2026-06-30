package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class MovimientoInventarioRequest {

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @PositiveOrZero(message = "El costo unitario no puede ser negativo")
    private Float costoUnitario;

    @Size(max = 250, message = "El motivo no puede superar 250 caracteres")
    private String motivo;

    @Size(max = 120, message = "La referencia no puede superar 120 caracteres")
    private String referencia;

    private Integer usuarioId;

    private Integer almacenId;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Float getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(Float costoUnitario) { this.costoUnitario = costoUnitario; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getAlmacenId() { return almacenId; }
    public void setAlmacenId(Integer almacenId) { this.almacenId = almacenId; }
}
