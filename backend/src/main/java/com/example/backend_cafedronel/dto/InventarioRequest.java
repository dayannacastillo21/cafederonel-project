package com.example.backend_cafedronel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class InventarioRequest {

    @NotBlank(message = "El nombre del insumo es obligatorio")
    @Size(max = 150, message = "El nombre del insumo no puede superar 150 caracteres")
    private String nombreInsumo;

    @Size(max = 50, message = "El codigo del insumo no puede superar 50 caracteres")
    private String codigoInsumo;

    @Size(max = 80, message = "La categoria no puede superar 80 caracteres")
    private String categoria;

    @Size(max = 120, message = "La ubicacion no puede superar 120 caracteres")
    private String ubicacion;

    private Integer almacenId;

    @Size(max = 80, message = "El lote no puede superar 80 caracteres")
    private String lote;

    private LocalDate fechaVencimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 30, message = "La unidad no puede superar 30 caracteres")
    private String unidad;

    @NotNull(message = "El stock minimo es obligatorio")
    @PositiveOrZero(message = "El stock minimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario no puede ser negativo")
    private Float precioUnitario;

    @NotBlank(message = "El proveedor es obligatorio")
    private String proveedor;

    private Boolean activo;

    public String getNombreInsumo() { return nombreInsumo; }
    public void setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }
    public String getCodigoInsumo() { return codigoInsumo; }
    public void setCodigoInsumo(String codigoInsumo) { this.codigoInsumo = codigoInsumo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Integer getAlmacenId() { return almacenId; }
    public void setAlmacenId(Integer almacenId) { this.almacenId = almacenId; }
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Float getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Float precioUnitario) { this.precioUnitario = precioUnitario; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
